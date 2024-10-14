package com.example.elkprac.product.entity;

import com.example.elkprac.common.exception.CustomException;
import com.example.elkprac.common.message.ErrorMessage;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ConnectionType {
    BLUETOOTH("블루투스", "블루투스"),
    AUX("aux", "AUX"),
    USB_C("C타입", "USB_C");

    private final String inputValue;
    private final String outputValue;
    private static final Map<String, ConnectionType> byInputValue = new HashMap<>();

    ConnectionType(String inputValue, String outputValue) {
        this.inputValue = inputValue;
        this.outputValue = outputValue;
    }

    static {
        for (ConnectionType connectionType : ConnectionType.values()) {
            byInputValue.put(connectionType.inputValue, connectionType);
        }
    }

    public static ConnectionType getByInputValue(String inputValue) {
        ConnectionType connectionType = byInputValue.get(inputValue);
        if (connectionType == null) {
            throw new CustomException(ErrorMessage.INVALID_CONNECTION_TYPE_NAME);
        }
        return connectionType;
    }
}
