package botapi.handler;

import botapi.types.Message;

/**
 * **************************************************************
 * <p>
 * Desc: 消息处理接口
 * User: jianguangluo
 * Date: 2018-12-27
 * Time: 15:20
 * <p>
 * **************************************************************
 */
public interface MessageHandler {
    /**
     * 处理消息
     *
     * @param message  消息体
     * @return
     */
    boolean execute(Message message);
}
