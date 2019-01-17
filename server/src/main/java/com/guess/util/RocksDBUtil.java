package com.guess.util;

import com.alibaba.fastjson.JSONObject;
import org.rocksdb.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RocksDBUtil {
    static String dbPath = "rocksdb/";//数据库文件目录
    //连接池
    final static ConcurrentHashMap<String, RocksDB> connetionPool = new ConcurrentHashMap();

    static {
        try {
            RocksDB.loadLibrary();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static synchronized RocksDB getDBConnetion(String tableName) {
        try {
            String dir = dbPath + tableName;
            RocksDB rocksDB = connetionPool.get(dir);
            if (rocksDB == null) {
                String path = RocksDBUtil.class.getClassLoader().getResource("/").getPath() + dir;
                System.out.println("path1:" + path);
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                Options options = new Options();
                options.setCreateIfMissing(true);
                rocksDB = RocksDB.open(options, path);
                if (rocksDB != null) {
                    connetionPool.put(dir, rocksDB);
                }
            }
            return rocksDB;


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static long writeDB(String tableName, String id, JSONObject detail) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                db.put(id.getBytes(), detail.toJSONString().getBytes());
                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public static long writeDBToString(String tableName, String id, String value) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                db.put(id.getBytes(), value.getBytes());
                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }


    public static long writeBatchDB(String tableName, WriteBatch batch) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                WriteOptions write_options = new WriteOptions();
                write_options.setDisableWAL(true);
                write_options.setSync(false);
                db.write(write_options, batch);
                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public static List<JSONObject> seekForPrev(String tableName, String preKey) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                List<JSONObject> list = new ArrayList<JSONObject>();
                RocksIterator iter = db.newIterator();
                iter.seekForPrev(preKey.getBytes());
                while (iter.isValid()) {
                    String key = new String(iter.key(), StandardCharsets.UTF_8);
                    String val = new String(iter.value(), StandardCharsets.UTF_8);
                    if (key.startsWith(preKey)) {
                        list.add(JSONObject.parseObject(val));
                    }
                    iter.next();
                }

                return list;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<JSONObject> getList(String tableName) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                List<JSONObject> list = new ArrayList<>();
                RocksIterator iter = db.newIterator();
                iter.seekToFirst();
                while (iter.isValid()) {
                    String key = new String(iter.key(), StandardCharsets.UTF_8);
                    String val = new String(iter.value(), StandardCharsets.UTF_8);
                    JSONObject record = JSONObject.parseObject(val);
                    list.add(record);
                    iter.next();
                }

                return list;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<String> getListByString(String tableName) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                List<String> list = new ArrayList<>();
                RocksIterator iter = db.newIterator();
                iter.seekToFirst();
                while (iter.isValid()) {
                    String key = new String(iter.key(), StandardCharsets.UTF_8);
                    String val = new String(iter.value(), StandardCharsets.UTF_8);
                    list.add(val);
                    iter.next();
                }

                return list;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject readDB(String tableName, String id) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                byte[] bytes = db.get(id.getBytes());
                if (bytes != null) {
                    return JSONObject.parseObject(new String(bytes));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String readDBByString(String tableName, String id) {

        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                byte[] bytes = db.get(id.getBytes());
                if (bytes != null) {
                    return new String(bytes);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void deleteDB(String tableName, String id) {
        try {
            RocksDB db = getDBConnetion(tableName);
            if (db != null) {
                db.delete(id.getBytes());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
