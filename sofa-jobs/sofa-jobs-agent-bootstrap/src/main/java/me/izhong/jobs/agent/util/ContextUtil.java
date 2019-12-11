package me.izhong.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@Lazy(false)
public class ContextUtil implements ApplicationContextAware, ApplicationListener {
	private static ApplicationContext context;
	private static String serverName, appName, runEnv , localIp;
	private static boolean isProd;
	// init只可能是单线程修改的，直接用可见性就可以保证
	private static volatile boolean init = false;

	static {
		String product_mode = System.getProperty("product_mode");
		if (StringUtils.equalsIgnoreCase(product_mode,"p")) {
			isProd = true;
		} else {
			isProd = false;
		}
		log.info("isProd初始化完成，isProd=" + isProd);
	}

//    static {
//        // reactor
//        Environment.initialize();
//    }

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		log.info("Initializing application context");
		ContextUtil.context = context;
	}

	@PreDestroy
	public void destroy(){
//		Environment.terminate();
	}

//    public static Environment getReactorEnv(){
//        return Environment.get();
//    }

	public static ApplicationContext getApplicationContext() {
		if (!init) {
			return null;
		}
		return context;
	}

	public static Object getBean(String name) {
		if (!init) {
			log.warn("应用正在启动中，获取spring容器对象失败:name={}", name);
			return null;
		}
		return context.getBean(name);
	}

	public static <T> T getBean(Class<T> clz) {
		if (!init) {
			log.warn("应用正在启动中，获取spring容器对象失败:clz={}", clz);
			return null;
		}
		return context.getBean(clz);
	}

	public static String getRunEnv() {
		if(runEnv != null)
			return runEnv;

		runEnv = System.getProperty("run_env");
		if (runEnv == null) {
			runEnv = "local";
		}
		return runEnv;
	}

	public static boolean isProd(){
		return isProd;
	}

	public static String getServerName() {
		if(serverName != null)
			return serverName;

		serverName = System.getProperty("SERVER_NAME");
		if (serverName == null) {
			serverName = "NO_NAME";
		}
		return serverName;
	}

	public static String getLocalIp() {
		if(localIp != null)
			return localIp;

		localIp = System.getProperty("LOCAL_IP");
		if (localIp == null) {
			localIp = "";
		}
		return localIp;
	}

	public static String getAppName() {
		if(appName != null)
			return appName;

		String serverName = getServerName();
		appName = StringUtils.stripEnd(serverName, "0123456789");
		return appName;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			init = true;
		}
	}
}
