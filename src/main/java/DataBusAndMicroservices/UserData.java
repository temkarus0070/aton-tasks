package DataBusAndMicroservices;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class UserData implements Comparable<UserData>, Serializable {

    private long account;
    private byte[] name;
    private double value;

    public UserData(long account, byte[] name, double value) {
        this.account = account;
        this.name = name;
        this.value = value;
    }

    public UserData() {
    }

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public byte[] getNameBytes() {
        return name;
    }

    public void setNameBytes(byte[] name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserData userData = (UserData) o;
        return account == userData.account && Double.compare(userData.value, value) == 0 && Arrays.equals(name, userData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, Arrays.hashCode(name), value);
    }


    @Override
    public int compareTo(UserData o) {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("account:%d name:%s value:%f", account, new String(name, StandardCharsets.UTF_8), value);
    }
}


