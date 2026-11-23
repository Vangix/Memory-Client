package com.vangix.tools.setting;

public class NumberSetting extends Setting<Double> {
    public final double min, max, step;
    public NumberSetting(String name, double def, double min, double max, double step) {
        super(name, def);
        this.min = min; this.max = max; this.step = step;
    }
    @Override public void set(Double v) {
        double snapped = Math.round(v / step) * step;
        super.set(Math.max(min, Math.min(max, snapped)));
    }
    public int intValue() { return (int) Math.round(value); }
}
