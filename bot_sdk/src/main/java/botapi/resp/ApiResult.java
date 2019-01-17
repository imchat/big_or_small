package botapi.resp;

import botapi.types.Chat;
import botapi.types.Message;
import botapi.types.Upload;
import botapi.types.User;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.List;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-29
 * Time: 17:45
 * <p>
 * **************************************************************
 */
public class ApiResult<T> {
    private boolean ok;
    private String description;
    private int errorCode;
    private T result;

    public static ApiResult build(boolean ok, String desc, int errorCode, Object result) {
        return new ApiResult(ok, desc, errorCode, result);
    }

    public static ApiResult build() {
        return new ApiResult(true, "SUCCESS", 0, null);
    }

    public ApiResult(boolean ok, String desc, int errorCode, T result) {
        this.ok = ok;
        this.description = desc;
        this.errorCode = errorCode;
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    public ApiResult setOk(boolean ok) {
        this.ok = ok;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public ApiResult setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public T getResult() {
        return result;
    }

    public ApiResult<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public enum ResultTypes {
        MESSAGE(new TypeReference<ApiResult<Message>>() {}.getType()),
        UPLOAD(new TypeReference<ApiResult<Upload>>() {}.getType()),
        LIST_OF_UPDATES(new TypeReference<ApiResult<List<Message>>>() {}.getType()),
        CHATS(new TypeReference<ApiResult<List<Chat>>>() {}.getType()),
        ME(new TypeReference<ApiResult<User>>(){}.getType());
        private Type type;

        ResultTypes(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }
}
