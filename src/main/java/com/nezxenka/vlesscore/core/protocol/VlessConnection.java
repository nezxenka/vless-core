package com.nezxenka.vlesscore.core.protocol;

public class VlessConnection {

    private byte version;
    private byte[] uuid;
    private String uuidString;
    private byte[] addonsData;
    private byte command;
    private int port;
    private AddressType addressType;
    private String address;
    private byte[] payload;
    private String authToken;

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
        this.uuidString = VlessUuidUtil.uuidToString(uuid);
    }

    public String getUuidString() {
        return uuidString;
    }

    public byte[] getAddonsData() {
        return addonsData;
    }

    public void setAddonsData(byte[] addonsData) {
        this.addonsData = addonsData;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isTcp() {
        return command == VlessConstants.CMD_TCP;
    }

    public boolean isUdp() {
        return command == VlessConstants.CMD_UDP;
    }

    public String getDestination() {
        return address + ":" + port;
    }

    @Override
    public String toString() {
        return (
            "VlessConnection{" +
            "uuid=" +
            uuidString +
            ", cmd=" +
            (isTcp() ? "TCP" : isUdp() ? "UDP" : "MUX") +
            ", dest=" +
            getDestination() +
            '}'
        );
    }
}
