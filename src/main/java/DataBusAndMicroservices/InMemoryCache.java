package DataBusAndMicroservices;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InMemoryCache {

    DB maker = DBMaker.heapDB().concurrencyDisable().cleanerHackEnable().make();
    private final Map<Long, UserData> accountCache = maker.<Long, UserData>treeMap("accounts")
                                                          .keySerializer(Serializer.LONG_DELTA)
                                                          .valueSerializer(Serializer.JAVA)
                                                          .create();
    private final Map<byte[], List<UserData>> nameCache =  maker.<byte[], List<UserData>>treeMap("names")
                                                                .keySerializer(Serializer.BYTE_ARRAY_DELTA)
                                                                .valueSerializer(Serializer.JAVA)
                                                                .create();
    private final Map<Double, List<UserData>> valueCache = maker.<Double, List<UserData>>treeMap("values")
                                                                .keySerializer(Serializer.DOUBLE)
                                                                .valueSerializer(Serializer.JAVA)
                                                                .create();

    public void add(UserData userData) {
         accountCache.put(userData.getAccount(), userData);
        putInNameCache(userData);
        putInValueCache(userData);
    }

    private void putInNameCache(UserData userData) {
        List<UserData> userDataByName = nameCache.get(userData.getNameBytes());
        if (userDataByName != null) {
            userDataByName.add(userData);
        } else {
            List<UserData> list = new ArrayList<>();
            list.add(userData);
            nameCache.put(userData.getNameBytes(), list);
        }
    }

    private void putInValueCache(UserData userData) {
        List<UserData> userDataByValue = valueCache.get(userData.getValue());
        if (userDataByValue != null) {
            userDataByValue.add(userData);
        } else {
            List<UserData> list = new ArrayList<>();
            list.add(userData);
            valueCache.put(userData.getValue(), list);
        }
    }


    public List<UserData> getAllByName(String name) {
        return nameCache.get(name.getBytes(StandardCharsets.UTF_8));
    }

    public List<UserData> getAllByValue(double value) {
        return valueCache.get(value);
    }


    public void update(long oldAccount, UserData newUserData) {
        UserData userDataForUpdate = accountCache.get(oldAccount);
        if (userDataForUpdate != null) {

            if (!Arrays.equals(newUserData.getNameBytes(), userDataForUpdate.getNameBytes())) {
                removeFromNameCache(userDataForUpdate);
                userDataForUpdate.setNameBytes(newUserData.getNameBytes());
                putInNameCache(userDataForUpdate);
            }
            if (newUserData.getValue() != userDataForUpdate.getValue()) {
                removeFromValueCache(userDataForUpdate);
                userDataForUpdate.setValue(newUserData.getValue());
                putInValueCache(userDataForUpdate);
            }
            if (newUserData.getAccount() != userDataForUpdate.getAccount()) {
                accountCache.remove(oldAccount);
                userDataForUpdate.setAccount(newUserData.getAccount());
                accountCache.put(newUserData.getAccount(), userDataForUpdate);
            }
        }
    }

    public void delete(long account) {
        UserData removedAccount = accountCache.remove(account);
        removeFromNameCache(removedAccount);
        removeFromValueCache(removedAccount);
    }

    public void removeFromNameCache(UserData userData) {
        List<UserData> userDataByName = nameCache.get(userData.getNameBytes());
        if (userDataByName != null) {
            userDataByName.remove(userData);
            if (userDataByName.isEmpty()) {
                nameCache.remove(userData.getNameBytes());
            }
        }

    }

    public void removeFromValueCache(UserData userData) {
        List<UserData> userDataByValue = valueCache.get(userData.getValue());
        userDataByValue.remove(userData);
        if (userDataByValue.isEmpty()) {
            valueCache.remove(userData.getValue());
        }

    }

    public UserData getByAccount(long account) {
        return accountCache.get(account);
    }
}
