package edu.school21.types;

public enum Mark {
    PLUS {
        @Override
        public String toString() {
            return "+";
        }
    },
    MINUS {
        @Override
        public String toString() {
            return "-";
        }
    },
    MULTIPLY {
        @Override
        public String toString() {
            return "*";
        }
    },
    DIVIDE {
        @Override
        public String toString() {
            return "/";
        }
    },
    MODULO {
        @Override
        public String toString() {
            return "%";
        }
    },
    POWER {
        @Override
        public String toString() {
            return "^";
        }
    },
    OPEN_PARENTHESIS {
        @Override
        public String toString() {
            return "(";
        }
    },
    CLOSE_PARENTHESIS {
        @Override
        public String toString() {
            return ")";
        }
    }
}
