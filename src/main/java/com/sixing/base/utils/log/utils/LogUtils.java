package com.sixing.base.utils.log.utils;

import com.sixing.base.utils.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志工具类
 */
@Component
public class LogUtils {

/*    @Autowired
    private DapQueueProducer dapQueueProducer;*/

    /**
     * 从异常实例中获取异常信息
     * 
     * @param t 异常实例
     */
    public String getErrorMsg(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer, true));
        return writer.toString();
    }

    public void error(Class<?> cls, Throwable t) {
        LogFactory.getLog(cls).error(t);
    }

    /**
     * @param cls 类对象
     * @param t 异常出错实例
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void error(Class<?> cls, Throwable t, boolean useMQ, String queue) {
        this.error(cls, t);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, getErrorMsg(t));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void error(Class<?> cls, String message) {
        LogFactory.getLog(cls).error(message);
    }

    /**
     * @param cls 类对象
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void error(Class<?> cls, String message, boolean useMQ, String queue) {
        this.error(cls, message);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void error(Class<?> cls, String message, Throwable t) {
        LogFactory.getLog(cls).error(message, t);
    }

    /**
     * @param cls 类对象
     * @param message 日志信息
     * @param t 异常出错实例
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void error(Class<?> cls, String message, Throwable t, boolean useMQ, String queue) {
        this.error(cls, message, t);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void error(String name, String message) {
        LogFactory.getLog(name).error(message);
    }

    /**
     * @param name 日志名称
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void error(String name, String message, boolean useMQ, String queue) {
        this.error(name, message);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void error(String name, String message, Throwable t) {
        LogFactory.getLog(name).error(message, t);
    }

    /**
     * @param name 日志名称
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void error(String name, String message, Throwable t, boolean useMQ, String queue) {
        this.error(name, message, t);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void info(Class<?> cls, String message) {
        if (LogFactory.getLog(cls).isInfoEnabled()) {
            LogFactory.getLog(cls).info(message);
        }
    }

    /**
     * @param cls 类对象
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void info(Class<?> cls, String message, boolean useMQ, String queue) {
        this.info(cls, message);
/*        if (useMQ && LogFactory.getLog(cls).isInfoEnabled()) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void info(String name, String message) {
        if (LogFactory.getLog(name).isInfoEnabled()) {
            LogFactory.getLog(name).info(message);
        }

    }

    /**
     * @param name 日志名称
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void info(String name, String message, boolean useMQ, String queue) {
        this.info(name, message);
/*        if (useMQ && Logger.getLogger(name).isInfoEnabled()) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void debug(Class<?> cls, String message) {
        if (LogFactory.getLog(cls).isDebugEnabled()) {
            LogFactory.getLog(cls).debug(message);
        }
    }

    /**
     * @param cls 类对象
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void debug(Class<?> cls, String message, boolean useMQ, String queue) {
        this.debug(cls, message);
/*        if (useMQ && LogFactory.getLog(cls).isDebugEnabled()) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void debug(String name, String message) {
        if (LogFactory.getLog(name).isDebugEnabled()) {
            LogFactory.getLog(name).debug(message);
        }
    }

    /**
     * @param name 日志名称
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void debug(String name, String message, boolean useMQ, String queue) {
        this.debug(name, message);
/*        if (useMQ && Logger.getLogger(name).isDebugEnabled()) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void warn(Class<?> cls, String message) {
        LogFactory.getLog(cls).warn(message);
    }

    /**
     * @param cls 类对象
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void warn(Class<?> cls, String message, boolean useMQ, String queue) {
        this.warn(cls, message);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void warn(String name, String message) {
        LogFactory.getLog(name).warn(message);
    }

    /**
     * @param name 日志名称
     * @param message 日志信息
     * @param useMQ 是否使用消息队列
     * @param queue 使用MQ时，需要指定的队列名称
     */
    public void warn(String name, String message, boolean useMQ, String queue) {
        this.warn(name, message);
/*        if (useMQ) {
            try {
                dapQueueProducer.send(queue, message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    /**
     * 格式化后输出
     * 
     * @param clazz     目标.Class
     * @param fmtString 输出信息key
     * @param value     输出信息value
     */
    public void fmtDebug(Class<?> clazz, String fmtString, Object... value) {
        if (LogFactory.getLog(clazz).isDebugEnabled() && StringUtils.isNotBlank(fmtString) && value != null) {
            fmtString = String.format(fmtString, value);
            this.debug(clazz, fmtString);
        }
    }

    /**
     * 格式化后输出
     * 
     * @param clazz     目标.Class
     * @param fmtString 输出信息key
     * @param value     输出信息value
     * @param useMQ     是否使用消息队列
     * @param queue     使用MQ时，需要指定的队列名称
     */
    public void fmtDebug(Class<?> clazz, String fmtString, boolean useMQ, String queue, Object... value) {
        if (LogFactory.getLog(clazz).isDebugEnabled() && StringUtils.isNotBlank(fmtString) && value != null) {
            fmtString = String.format(fmtString, value);
            this.debug(clazz, fmtString, useMQ, queue);
        }
    }

}