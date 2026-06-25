package com.nezxenka.vlesscore.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

public class SystemMetrics {

    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    public SystemMetrics() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }

    public double getCpuLoad() {
        return osBean.getSystemLoadAverage();
    }

    public long getUsedHeapMemory() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    public long getMaxHeapMemory() {
        return memoryBean.getHeapMemoryUsage().getMax();
    }

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public String getMemorySummary() {
        long used = getUsedHeapMemory();
        long max = getMaxHeapMemory();
        return String.format(
            "%.1f MB / %.1f MB",
            used / (1024.0 * 1024),
            max / (1024.0 * 1024)
        );
    }

    public String getSystemSummary() {
        return String.format(
            "CPU cores: %d | Load avg: %.2f | Memory: %s",
            getAvailableProcessors(),
            getCpuLoad(),
            getMemorySummary()
        );
    }
}
