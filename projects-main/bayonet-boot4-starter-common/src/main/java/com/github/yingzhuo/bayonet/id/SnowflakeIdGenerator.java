package com.github.yingzhuo.bayonet.id;

/**
 * 雪花ID生成器 (Snowflake)。
 *
 * <p>生成分布式全局唯一 64 位长整型 ID，结构如下：</p>
 *
 * <pre>
 *  0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 *  ───────────────────────────────┬─────────────────────────┬──────┬──────┬──────────
 *              1位符号位(0)        41位时间戳(毫秒)         5位DC  5位WK   12位序列号
 * </pre>
 *
 * <ul>
 *   <li>符号位：始终为 0（正数）</li>
 *   <li>时间戳：从 2020-01-01 起的毫秒数，可用约 69 年</li>
 *   <li>数据中心 ID：5 位，最大支持 32 个</li>
 *   <li>工作节点 ID：5 位，最大支持 32 个</li>
 *   <li>序列号：12 位，同一毫秒内最多生成 4096 个 ID</li>
 * </ul>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class SnowflakeIdGenerator {

    /**
     * 起始时间戳 (2020-01-01 00:00:00 UTC)
     */
    private static final long EPOCH = 1577836800000L;

    /**
     * 机器ID所占位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 数据中心ID所占位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 支持的最大机器ID (0~31)
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据中心ID (0~31)
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 序列号所占位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID左移12位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID左移17位 (12+5)
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳左移22位 (12+5+5)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 序列号掩码 (4095)
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 时钟回拨最大容忍毫秒数
     */
    private static final long MAX_CLOCK_BACKWARD_MILLIS = 10L;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 构造函数。
     *
     * @param datacenterId 数据中心ID (0~31)
     * @param workerId     工作机器ID (0~31)
     * @throws IllegalArgumentException 参数超出范围时抛出
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId must be between 0 and " + MAX_DATACENTER_ID);
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId must be between 0 and " + MAX_WORKER_ID);
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 返回当前系统时间的毫秒数。
     *
     * @return 当前时间戳（毫秒）
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取下一个唯一 ID（线程安全）。
     *
     * <p>在同一毫秒内通过序列号区分不同 ID（最多 4096 个/毫秒），
     * 序列号耗尽时自旋等待下一毫秒。该实现可容忍不超过 {@value #MAX_CLOCK_BACKWARD_MILLIS} 毫秒的时钟回拨。</p>
     *
     * @return 全局唯一 64 位 Long 型 ID
     * @throws IllegalStateException 时钟回拨超过容忍上限时抛出
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 时钟回拨处理：当前时间小于上次生成ID的时间，说明发生了时钟回拨
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= MAX_CLOCK_BACKWARD_MILLIS) {
                // 自旋等待直到追上最后时间
                while (timestamp < lastTimestamp) {
                    Thread.onSpinWait();
                    timestamp = timeGen();
                }
            } else {
                throw new IllegalStateException(String.format(
                        "Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
            }
        }

        // 同一毫秒内，序列号递增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 当前毫秒序列号用完，等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组合ID：时间戳差值 + 数据中心ID + 工作节点ID + 序列号
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 获取下一个唯一 ID 的字符串形式（线程安全）。
     *
     * <p>内部调用 {@link #nextId()} 并将结果转为十进制字符串。</p>
     *
     * @return 全局唯一 64 位 Long 型 ID 的十进制字符串表示
     * @throws IllegalStateException 时钟回拨超过容忍上限时抛出
     */
    public String nextIdAsString() {
        return String.valueOf(nextId());
    }

    /**
     * 阻塞到下一毫秒，直到获得新的时间戳。
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();
            timestamp = timeGen();
        }
        return timestamp;
    }
}
