package botapi.handler;

import botapi.types.Message;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-30
 * Time: 02:19
 * <p>
 * **************************************************************
 */
public interface HandlerFactory {
     boolean execute(Message message);
}
