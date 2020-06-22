package com.sixing.base.utils.id;

public class IdWorker {

    private static final long twepoch            = 1288834974657L;
    private static final long workerIdBits       = 5L;
    private static final long datacenterIdBits   = 5L;
    private static final long maxProviderId      = -1L ^ (-1L << workerIdBits);
    private static final long maxDataCenterId    = -1L ^ (-1L << datacenterIdBits);
    private static final long sequenceBits       = 12L;
    private static final long workerIdShift      = sequenceBits;
    private static final long dataCenterIdShift  = sequenceBits + workerIdBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static final long sequenceMask       = -1L ^ (-1L << sequenceBits);

//    private static long       providerId;
//    private static long       dataCenterId;
    private static long       sequence           = 0L;
    private static long       lastTimestamp      = -1L;

    private IdWorker() {

    }

    private IdWorker(long providerId, long dataCenterId) {
        if (providerId > maxProviderId || providerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxProviderId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", maxDataCenterId));
        }
//        this.providerId = providerId;
//        this.dataCenterId = dataCenterId;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public synchronized static long generateId(long providerId, long dataCenterId) {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (providerId << workerIdShift) | sequence;
    }
}