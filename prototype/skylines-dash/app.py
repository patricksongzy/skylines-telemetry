from dash import Dash, html, dcc
from dash.dependencies import Input, Output, State
from dash_extensions import WebSocket
from psycopg_pool import ConnectionPool
import json
import math
import random
import dash_bootstrap_components as dbc
import plotly.express as px
import plotly.graph_objects as go
import pandas as pd

pool = ConnectionPool("postgresql://postgres:password@timescale:5432/skylinestelemetry")
app = Dash(__name__, external_stylesheets=[dbc.themes.SLATE], meta_tags=[{'name': 'viewport', 'content': 'width=device-width, initial-scale=1'}])

def make_figure():
    return go.Figure().update_layout(
        template = 'plotly_dark',
        plot_bgcolor = 'rgba(0, 0, 0, 0)',
        paper_bgcolor = 'rgba(0, 0, 0, 0)',
        autosize = True,
        margin = dict(l=0, r=0, b=0, t=50),
    )

@app.callback(
    [Output('dispatch', 'children'), Output('response', 'children'), Output('transport', 'children'), Output('trip', 'children')],
    [Input('interval', 'n_intervals')]
)
def update_times(n):
    with pool.connection() as con:
        df = pd.read_sql_query("""
        select sum(enroute_time - incident.time) as to_dispatch_sum, sum(trip.time - enroute_time) as to_segment_sum, count(state) as state_count, state
        from incident left join ambulance_trip_segment as trip on trip.trip_id like '%-' || incident.citizen_id::text
        where in_ambulance = true
        group by state
        """, con)
        df.to_dispatch_sum = pd.to_timedelta(df.to_dispatch_sum)
        df.to_segment_sum = pd.to_timedelta(df.to_segment_sum)
        arrived = df[df.state.isin(['RETURN_FULL', 'RETURN_EMPTY', 'WAIT'])]
        returned = df[df.state.isin(['DESTROYED'])]

        dispatch = None
        response = None
        transport = None
        trip = None
        if not df.empty:
            dispatch = df.to_dispatch_sum.sum() / df.state_count.sum()
            if not arrived.empty:
                response = arrived.to_segment_sum.sum() / arrived.state_count.sum()
            # TODO use destroyed - emergency?
            if not returned.empty:
                trip = returned.to_segment_sum.sum() / returned.state_count.sum()
            if response is not None and trip is not None:
                transport = trip - response

        dispatch = dispatch if dispatch is not None else "-"
        response = response if response is not None else "-"
        transport = transport if transport is not None else "-"
        trip = trip if trip is not None else "-"
        return [str(dispatch), str(response), str(transport), str(trip)]

# @app.callback(Output('positions', 'figure'), [Input('interval-long', 'n_intervals')])
def update_positions(n):
    with pool.connection() as con:
        df = pd.read_sql_query("""
        select time, x, y, ambulance_id from (select time, x, y, ambulance_id, row_number() over(partition by ambulance_id order by time desc) from ambulance_data
        where ambulance_id in (select ambulance_id
            from (select distinct on (ambulance_id) ambulance_id, state from ambulance_trip_segment order by ambulance_id, time desc) as states
            where state != 'DESTROYED' and state != 'STALE')
            order by ambulance_id, time desc) as partitioned
            where (row_number - 1) % 100 = 0 and row_number <= 500
        """, con)
        df.time = pd.to_datetime(df.time)
        positions = make_figure().update_layout(title='Emergency Locations (red = emergency, green = returning, blue = transporting)', yaxis=dict(scaleanchor='x', scaleratio=1), uirevision=False)
        df = pd.pivot_table(df, values=['x', 'y'], index='time', columns='ambulance_id', aggfunc='mean').reorder_levels([1, 0], axis=1).sort_index(axis=1)

        states = pd.read_sql_query("""
        (select ambulance_id, state from (select distinct on (ambulance_id) ambulance_id, state from ambulance_trip_segment order by ambulance_id, time desc) as states
            where state != 'DESTROYED' and state != 'STALE')
        """, con)

        if df.empty:
            return positions

        # label_x = df[-1:].stack()[:1].values[0]
        # label_y = df[-1:].stack()[-1:].values[0]
        for ambulance in df.columns.get_level_values(level=0).drop_duplicates():
            color = 'rgb(195, 195, 195)'
            state = states[states.ambulance_id == ambulance].state.values[0]
            if (state == 'EMERGENCY'):
                color = 'rgb(208, 52, 44)'
            elif (state == 'RETURN_FULL'):
                color = 'rgb(173, 216, 230)'
            elif (state == 'RETURN_EMPTY'):
                color = 'rgb(52, 208, 44)'
            positions.add_trace(go.Scattergl(
                x = df[ambulance].x.values,
                y = df[ambulance].y.values,
                name = ambulance,
                mode = 'lines',
                line = dict(dash='dot', color=color),
            ))
            theta = random.random() * 2 / 3 * math.pi
            if random.random() > 0.5:
                theta = 2 * math.pi - theta
            if len(df[ambulance].index) > 1:
                dx = df[ambulance].iloc[-1].x - df[ambulance].iloc[-2].x
                dy = df[ambulance].iloc[-1].y - df[ambulance].iloc[-2].y
                dt = math.atan(dy / dx)
                dt = math.pi - dt if dx < 0 else 2 * math.pi - dt
                theta += dt
            positions.add_annotation(
                x = df[ambulance].iloc[-1].x,
                y = df[ambulance].iloc[-1].y,
                ax = math.cos(theta) * 35,
                ay = math.sin(theta) * 35,
                text = ambulance,
                showarrow = True,
                arrowhead = 7,
                arrowsize = 1.15,
                arrowwidth = 0.85,
                arrowcolor = 'rgb(195, 195, 195)',
                font = dict(color='rgb(195, 195, 195)'),
            )

        # x_cut = pd.cut(label_x, 3)
        # y_cut = pd.cut(label_y, 3)
        # for (x, y) in zip(label_x, label_y):

        return positions

@app.callback(Output('speeds', 'figure'), [Input('interval', 'n_intervals')], State('speeds', 'figure'))
def update_speeds(n, figure):
    with pool.connection() as con:
        df = pd.read_sql_query("""
        select time, ambulance_id, speed from 
            (select time_bucket('1 hour', time) as time, ambulance_id, speed, row_number() over(partition by ambulance_id order by time desc) from ambulance_data
            where ambulance_id in (select ambulance_id
            from (select distinct on (ambulance_id) ambulance_id, state from ambulance_trip_segment order by ambulance_id, time desc) as states
            where state = 'EMERGENCY') order by ambulance_id, time desc) as aggregated
            where row_number <= 500
        """, con)
        df.time = pd.to_datetime(df.time)
        speeds = make_figure().update_layout(title='Emergency Speeds')
        df = pd.pivot_table(df, values='speed', index='time', columns='ambulance_id', aggfunc='mean')
        colors = px.colors.qualitative.Plotly
        colorIndex = random.randint(0, len(colors) - 1)
        for ambulance in df.columns:
            color = colors[colorIndex]
            if figure is not None:
                cached = list(filter(lambda x: (x['name'] == str(ambulance)), figure['data']))
                if cached:
                    color = cached[0]['line']['color']
                else:
                    colorIndex = (colorIndex + 1) % len(colors)
            else:
                colorIndex = (colorIndex + 1) % len(colors)
            speeds.add_trace(go.Scattergl(
                x = df.index,
                y = df[ambulance].values,
                name = ambulance,
                mode = 'markers+lines',
                line = dict(color=color),
                # line = dict(shape='spline'),
            ))

        return speeds

app.clientside_callback(
    """
    function (message, data) {
        if (window.lastUpdates == null) {
            window.lastUpdates = {};
        }
        if (window.positionsFlag == null) {
            window.positionsFlag = false;
        }
        window.positionsFlag = !window.positionsFlag;
        if (message != null && window.positionsFlag) {
            const positionGraph = document.getElementById('positions').getElementsByClassName('js-plotly-plot')[0];
            const speedGraph = document.getElementById('speeds').getElementsByClassName('js-plotly-plot')[0];

            const parsed = JSON.parse(message['data']);
            let processed = new Set();

            let annotations = positionGraph.layout.annotations;
            if (annotations == null) {
                annotations = [];
            }
            let x = [];
            let y = [];
            let positionIndices = [];
            let positionDeleteIndices = [];
            let currentTime = new Date(Object.values(parsed).find(telemetry => telemetry.timestamp != null).timestamp);
            positionGraph.data.forEach((trace, i) => {
                if (trace.name in parsed) {
                    let telemetry = parsed[trace.name];
                    window.lastUpdates[trace.name] = telemetry.timestamp;
                    const isReturned = telemetry.arriving && (telemetry.state == 'RETURN_FULL' || telemetry.state == 'RETURN_EMPTY');
                    // TODO - convert to reading from ambulance state kafka topic
                    /* TODO - temporary solution
                    let isStaled = window.lastUpdates[trace.name] != null && currentTime != null
                        && currentTime - new Date(window.lastUpdates[trace.name]) > 10800000;*/
                    if (!isReturned/* || isStaled*/) {
                        annotations[i].x = telemetry.x;
                        annotations[i].y = telemetry.z;
                        x.push([telemetry.x]);
                        y.push([telemetry.z]);
                        positionIndices.push(i);
                    } else {
                        positionDeleteIndices.push(i);
                        delete window.lastUpdates[trace.name];
                    }
                    processed.add(trace.name);
                }
            });

            let annotationCursor = 0;
            // remove deleted annotations
            annotations = annotations?.filter((annotation, index) => {
                while (index > positionDeleteIndices[annotationCursor]) {
                    annotationCursor += 1;
                }

                if (index == positionDeleteIndices[annotationCursor]) {
                    annotationCursor += 1;
                    return false;
                }

                return true;
            });

            let positionUpdate = window.dash_clientside.no_update;
            if (positionIndices.length > 0) {
                positionUpdate = [{x: x, y: y}, positionIndices];
            }

            Plotly.extendTraces(positionGraph, {x: x, y: y}, positionIndices, 10);
            Plotly.deleteTraces(positionGraph, positionDeleteIndices);

            let positionAdded = [];
            let annotationsAdded = [];
            let positionAddedIndices = [];
            let addedKeys = [];
            for (const [key, telemetry] of Object.entries(parsed)) {
                if (processed.has(key)) { continue; }
                addedKeys.push(key);
                window.lastUpdates[key] = telemetry.timestamp;
                color = 'rgb(195, 195, 195)'
                if (telemetry.state == 'EMERGENCY') { color = 'rgb(208, 52, 44)' }
                else if (telemetry.state == 'RETURN_FULL') { color = 'rgb(173, 216, 230)' }
                else if (telemetry.state == 'RETURN_EMPTY') { color = 'rgb(52, 208, 44)' }
                positionAdded.push({
                    x: [telemetry.x],
                    y: [telemetry.y],
                    name: key,
                    mode: 'lines',
                    line: {
                        dash: 'dot',
                        color: color
                    }
                });
                let r = Math.floor(Math.random() * 2);
                let theta = Math.random() * 2 / 3 * Math.PI
                annotationsAdded.push({
                    x: telemetry.x,
                    y: telemetry.z,
                    ax: Math.cos(theta) * 35,
                    ay: Math.sin(theta) * 35,
                    text: key,
                    showarrow: true,
                    arrowhead: 7,
                    arrowsize: 1.15,
                    arrowwidth: 0.85,
                    arrowcolor: 'rgb(195, 195, 195)',
                    font: {
                        color: 'rgb(195, 195, 195)'
                    }
                });
            }
            let currentKeys = positionGraph.data.map(telemetry => parseInt(telemetry.name));
            let currentKeysIndex = 0;
            let addedKeysIndex = 0;
            while (addedKeysIndex < addedKeys.length && currentKeysIndex < currentKeys.length) {
                if (currentKeys[currentKeysIndex] >= addedKeys[addedKeysIndex]) {
                    positionAddedIndices.push(currentKeysIndex);
                    addedKeysIndex += 1;
                }
                currentKeysIndex += 1;
            }
            while (addedKeysIndex < addedKeys.length) {
                positionAddedIndices.push(currentKeysIndex);
                addedKeysIndex += 1;
                currentKeysIndex += 1;
            }
            let annotationIndex = 0;
            for (const index of positionAddedIndices) {
                annotations.splice(index, 0, annotationsAdded[annotationIndex]);
                annotationIndex += 1;
            }
            Plotly.addTraces(positionGraph, positionAdded, positionAddedIndices);
            let layout = positionGraph.layout;
            layout.annotations = annotations;
            Plotly.relayout(positionGraph, layout);
            
            /*let speedUpdate = [];
            speedGraph.data.filter(telemetry => {
                if (telemetry.name in parsed) {
                    if (parsed[telemetry.name].state == 'EMERGENCY') {
                        if (telemetry.x.length > 25) {
                            telemetry.x.shift();
                            telemetry.y.shift();
                        }
                        telemetry.x.push(parsed[telemetry.name].timestamp);
                        telemetry.y.push(parsed[telemetry.name].speed);
                        speedUpdate.push(telemetry);
                        delete parsed[telemetry.name];
                    }
                } else {
                    speedUpdate.push(telemetry);
                }
            });
            for (const [key, telemetry] of Object.entries(parsed)) {
                if (telemetry.state == 'EMERGENCY') {
                    speedUpdate.push({
                        name: key,
                        mode: 'markers+lines',
                        type: 'scattergl',
                        x: [telemetry.timestamp],
                        y: [telemetry.speed],
                        //line: {
                        //    shape: 'spline'
                        //}
                    });
                }
            }
            Plotly.react(speedGraph, { data: speedUpdate, layout: speedGraph.layout, config: speedGraph.config });
            const graphs = document.getElementsByClassName('js-plotly-plot');*/
        }

        return {};
    }
    """,
    Output('none', 'data'),
    [Input('ws', 'message')]
)

def make_empty_indicator(title):
    return make_figure().add_trace(go.Indicator(
        mode = 'number',
        title = { 'text': title },
        value = None,
    ))

@app.callback(
    [
        Output('emergency', 'figure'), Output('wait', 'figure'), Output('return_full', 'figure'), Output('return_empty', 'figure'),
        Output('assigned', 'figure'), Output('available', 'figure'), Output('availability', 'figure'), Output('total', 'figure'),
    ],
    [Input('interval', 'n_intervals')]
)
def update_states(n):
    with pool.connection() as con:
        df = pd.read_sql_query("""
        with amounts as (
        select bucket, state, count(state) as amount
        from (
            select distinct on (bucket, ambulance_id) bucket, state from (select time_bucket('1 day', time) as bucket from ambulance_trip_segment) as t join
            (select time, ambulance_id, last(state, time) over (partition by ambulance_id, date_trunc('day', time)) as state from ambulance_trip_segment) as s
            on date_trunc('day', time) <= bucket
            order by bucket, ambulance_id, time desc
        ) as states
        group by bucket, state
        ) select b.bucket as time, v.state as state, coalesce(amount, 0) as amount
        from (select distinct bucket from amounts) b
        cross join (values ('EMERGENCY'), ('WAIT'), ('RETURN_EMPTY'), ('RETURN_FULL'), ('DESTROYED'), ('STALE')) v(state)
        left join amounts joined
        on joined.bucket = b.bucket and joined.state = v.state
        order by time asc
        """, con)
        df.time = pd.to_datetime(df.time)

        ambulances = pd.read_sql_query("select sum(ambulances) from hospital", con)
        assigned = None
        available = None
        availability = None
        total = ambulances['sum'].values[0]
        assigned = df[~df.state.isin(['DESTROYED', 'STALE'])].groupby(df.time).sum(numeric_only=True).reset_index()
        if hasattr(assigned, 'amount'):
            if total is not None:
                available = assigned.copy()
                available.amount = total - available.amount
                availability = available.copy()
                availability.amount = availability.amount / total * 100
        else:
            assigned = None

        emergency = make_state_indicator(df[df.state == 'EMERGENCY'], 'Responding', total)
        wait = make_state_indicator(df[df.state == 'WAIT'], 'Treating on scene', total)
        return_full = make_state_indicator(df[df.state == 'RETURN_FULL'], 'Transporting', total)
        return_empty = make_state_indicator(df[df.state == 'RETURN_EMPTY'], 'Returning', total)
        if assigned is not None:
            assigned = make_state_indicator(assigned, 'Assigned', total)
        else:
            assigned = make_empty_indicator('Assigned')
        if available is not None:
            available = make_state_indicator(available, 'Available', total)
        else:
            available = make_empty_indicator('Available')
        total = make_figure().add_trace(go.Indicator(
            mode = 'number',
            title = { 'text': 'Total' },
            value = total,
        ))
        if availability is not None:
            availability = make_state_indicator(availability, 'Availability', 100).update_traces(
                number = dict(suffix = '%'),
                selector = dict(type='indicator')
            ).update_traces(
                hovertemplate = '%{x}: %{y}%<extra></extra>',
                selector = dict(type = 'scatter')
            )
        else:
            availability = make_empty_indicator('Availability')

        return [emergency, wait, return_full, return_empty, assigned, available, availability, total]

def make_state_indicator(df, title, ymax):
    state = make_figure().update_layout(
        xaxis = dict(showgrid=False, zeroline=False, visible=False),
        yaxis = dict(showgrid=False, zeroline=False, visible=False),
        yaxis_range=[0, ymax],
        hovermode = 'closest',
        hoverdistance = 100,
    ).update_yaxes(rangemode='tozero')

    state.add_trace(go.Indicator(
        mode = 'number',
        title = { 'text': title },
        value = df[df.time == df.time.max()].amount.sum(),
    ))

    state.add_trace(go.Scatter(
        x = df.time,
        y = df.amount,
        hovertemplate = "%{x}: %{y}<extra></extra>",
        fill = 'tozeroy',
        mode = 'lines'
    ))

    return state

def make_plot(id, figure, height):
    return dbc.Card([
        dbc.CardBody([
            dcc.Graph(id=id, figure=figure, style={'height': height}, animate=False),
        ], class_name='h-100'),
    ], class_name='h-100')

def make_text_card(id, title, data):
    return dbc.Card([
        dbc.CardBody([
            html.Div([html.H4(title), html.H1(data, id=id)], style={'height': '10vh'}),
        ], class_name='h-100'),
    ], class_name='h-100')

states = update_states(0)
positions = update_positions(0)
speeds = update_speeds(0, None)
times = update_times(0)
app.layout = dbc.Container([
    WebSocket(id='ws', url='ws://192.168.2.115:8080/skylines-websocket-0.1.0/ws/ambulance_telemetry'),
    dcc.Interval(id='interval-long', interval=10000),
    dcc.Interval(id='interval', interval=5000),
    dcc.Store(id='none'),
    dbc.Row([
        dbc.Col([make_plot('emergency', states[0], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('wait', states[1], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('return_full', states[2], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('return_empty', states[3], '15vh')], xs=6, sm=3),
    ], class_name='gy-3'),
    html.Br(),
    dbc.Row([
        dbc.Col([make_plot('assigned', states[4], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('available', states[5], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('availability', states[6], '15vh')], xs=6, sm=3),
        dbc.Col([make_plot('total', states[7], '15vh')], xs=6, sm=3),
    ], class_name='gy-3'),
    html.Br(),
    dbc.Row([
        dbc.Col([make_text_card('dispatch', 'Mean Dispatch Time', times[0])], xs=6, sm=3),
        dbc.Col([make_text_card('response', 'Mean Response Time', times[1])], xs=6, sm=3),
        dbc.Col([make_text_card('transport', 'Mean Transport Time', times[2])], xs=6, sm=3),
        dbc.Col([make_text_card('trip', 'Mean Time to Hospital', times[3])], xs=6, sm=3),
    ], class_name='gy-3'),
    html.Br(),
    dbc.Row([
        dbc.Col([make_plot('speeds', speeds, '45vh')], lg=6),
        dbc.Col([make_plot('positions', positions, '45vh')], lg=6),
    ], class_name='gy-3'),
],  fluid=True, style={'minHeight': '100vh'}, class_name='py-3')

if __name__ == '__main__':
    app.run_server(host='0.0.0.0', debug=False)
