use kafka::consumer::Consumer;
use kafka::error::Error as KafkaError;

fn main() {
    if let Err(e) = consume() {
        println!("error consuming messages: {}", e);
    }
}

fn consume() -> Result<(), KafkaError> {
    let mut consumer = Consumer::from_hosts(vec!["localhost:9092".to_owned()])
        .with_topic("ambulances".to_owned())
        .create()?;
    loop {
        match consumer.poll() {
            Ok(mss) => {
                for ms in mss.iter() {
                    for m in ms.messages() {
                        println!("{} - {:?} {:?}", ms.topic(), m.key, std::str::from_utf8(m.value).unwrap());
                    }
                }
            },
            Err(error) => println!("error consuming message: {}", error),
        }

        consumer.commit_consumed()?;
    }
}
