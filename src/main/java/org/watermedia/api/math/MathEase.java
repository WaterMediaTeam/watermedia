package org.watermedia.api.math;

import org.watermedia.api.MathAPI;

public enum MathEase {
    EASE_IN {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeIn(start, end, value);
        }
    },
    EASE_OUT {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOut(start, end, value);
        }
    },
    EASE_IN_OUT {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOut(start, end, value);
        }
    },
    EASE_OUT_IN {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutIn(start, end, value);
        }
    },
    EASE_IN_SINE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInSine(start, end, value);
        }
    },
    EASE_OUT_SINE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutSine(start, end, value);
        }
    },
    EASE_IN_OUT_SINE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutSine(start, end, value);
        }
    },
    EASE_IN_CUBIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInCubic(start, end, value);
        }
    },
    EASE_OUT_CUBIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutCubic(start, end, value);
        }
    },
    EASE_IN_OUT_CUBIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutCubic(start, end, value);
        }
    },
    EASE_IN_QUAD {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInQuad(start, end, value);
        }
    },
    EASE_OUT_QUAD {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutQuad(start, end, value);
        }
    },
    EASE_IN_OUT_QUAD {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutQuad(start, end, value);
        }
    },
    EASE_IN_ELASTIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInElastic(start, end, value);
        }
    },
    EASE_OUT_ELASTIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutElastic(start, end, value);
        }
    },
    EASE_IN_OUT_ELASTIC {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutElastic(start, end, value);
        }
    },
    EASE_IN_QUINT {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInQuint(start, end, value);
        }
    },
    EASE_OUT_QUINT {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutQuint(start, end, value);
        }
    },
    EASE_IN_OUT_QUINT {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutQuint(start, end, value);
        }
    },
    EASE_IN_CIRCLE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInCircle(start, end, value);
        }
    },
    EASE_OUT_CIRCLE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutCircle(start, end, value);
        }
    },
    EASE_IN_OUT_CIRCLE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutCircle(start, end, value);
        }
    },
    EASE_IN_EXPO {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInExpo(start, end, value);
        }
    },
    EASE_OUT_EXPO {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutExpo(start, end, value);
        }
    },
    EASE_IN_OUT_EXPO {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutExpo(start, end, value);
        }
    },
    EASE_IN_BACK {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInBack(start, end, value);
        }
    },
    EASE_OUT_BACK {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutBack(start, end, value);
        }
    },
    EASE_IN_OUT_BACK {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutBack(start, end, value);
        }
    },
    EASE_IN_BOUNCE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInBounce(start, end, value);
        }
    },
    EASE_OUT_BOUNCE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeOutBounce(start, end, value);
        }
    },
    EASE_IN_OUT_BOUNCE {
        @Override
        public double apply(double start, double end, double value) {
            return MathAPI.easeInOutBounce(start, end, value);
        }
    };

    public abstract double apply(double start, double end, double value);
}