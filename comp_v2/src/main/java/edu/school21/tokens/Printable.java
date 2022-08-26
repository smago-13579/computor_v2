package edu.school21.tokens;

import java.util.List;

public interface Printable {
    String getValueToString();
    void setValue(List<Token> value);
}
