package com.example.eventbritetest.network;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum DistanceUnit {
    KM,
    MI;

    public static DistanceUnit getUnit(String value) {
        if(value == null || value.isEmpty())
            return KM;
        return DistanceUnit.valueOf(value);
    }

    public static List<Unit> getUnitList() {
        Unit kmUnit = new Unit(DistanceUnit.KM.ordinal(), DistanceUnit.KM.toString(), false);
        Unit miUnit = new Unit(DistanceUnit.MI.ordinal(), DistanceUnit.MI.toString(), false);
        List<Unit> units = new ArrayList<>();
        units.add(kmUnit);
        units.add(miUnit);
        return units;
    }

    public static class Unit {
        private int id;
        private String value;
        private boolean isChecked;

        public Unit(int id, String value, boolean isChecked) {
            this.id = id;
            this.value = value;
            this.isChecked = isChecked;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(!(obj instanceof Unit))
                return false;

            Unit unit = (Unit) obj;
            return this.id == unit.id && this.value == unit.value;

        }
    }
}
