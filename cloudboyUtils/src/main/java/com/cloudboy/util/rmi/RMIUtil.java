package com.cloudboy.util.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIUtil {
	public static int DEFAULT_RMI_REGISTRY_PORT = 1099;
	
	/**
	 * 获取本地的RMI registry, 如果还没启动，则使用默认端口1099启动之。
	 * @return RMI registry的stub.
	 * @throws RemoteException
	 */
	public static Registry startLocalRMIRegistry() throws RemoteException {
        return startLocalRMIRegistry(DEFAULT_RMI_REGISTRY_PORT);
    }
	
	/**
	 * 获取本地的RMI registry, 如果还没启动，则使用端口port启动之。
	 * @return RMI registry的stub.
	 * @throws RemoteException
	 */
	public static Registry startLocalRMIRegistry(int port) throws RemoteException {
        Registry registry = null;
        try {
            // The following code in the server obtains a stub for a registry on the local host and default registry port(1099)
            registry = LocateRegistry.getRegistry();
            registry.list();
        } catch (RemoteException e) {
        	// 如果Registry没启动，产生一个（也可以在命令行中，使用命令:rmiregistry启动rmi registry）
            registry = LocateRegistry.createRegistry(port);
        }
        return registry;
    }
}
