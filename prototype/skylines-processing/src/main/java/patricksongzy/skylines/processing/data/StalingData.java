package patricksongzy.skylines.processing.data;

public class StalingData<T> {
    private long key;
    private T data;

    public StalingData() {}

    public StalingData(long key, T data) {
        this.key = key;
        this.data = data;
    }

    public long getKey() {
        return key;
    }

    public T getData() {
        return data;
    }
}
