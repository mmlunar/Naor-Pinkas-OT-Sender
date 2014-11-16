package ot.naorpinkas.networkconnection;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ot.naorpinkas.messageprocessor.SenderMessageProcessor;
import ot.naorpinkas.utility.SenderConstant;


public class SenderServerConnection extends UnicastRemoteObject implements IExchangeData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int thePort = SenderConstant.COMMUNICATION_PORT;

	private Registry registry;

	private SenderMessageProcessor senderMessageProcessor = new SenderMessageProcessor();


	public SenderServerConnection() throws RemoteException {
		super(0);    
	}

	
	
	@Override
	public byte[][] getEncryptedMessageChain(BigInteger pk)
			throws RemoteException {
		// TODO Auto-generated method stub
		byte[][] cryptoMessage;
		cryptoMessage = new byte[SenderConstant.TOTAL_CHOOSERS][];
		try {
			cryptoMessage = senderMessageProcessor.sendEncryptedMessageChainToChooser(pk);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cryptoMessage;
	}
	
	@Override
	public BigInteger getSenderPublicDataPrime() throws RemoteException {
		// TODO Auto-generated method stub
		return senderMessageProcessor.sendPublicDataToChooser().getPrime();
	}
	
	@Override
	public BigInteger[] getSenderPublicDataConstant() throws RemoteException {
		// TODO Auto-generated method stub
		return senderMessageProcessor.getSenderPublicDataConstant();
	}
	
	@Override
	public BigInteger getSenderPublicDataGenerator() throws RemoteException {
		// TODO Auto-generated method stub
		return senderMessageProcessor.getSenderPublicDataGenerator();
	}
	
	@Override
	public BigInteger getSenderPublicDataGeneratorWithRandom()
			throws RemoteException {
		// TODO Auto-generated method stub
		return senderMessageProcessor.getSenderPublicDataGeneratorWithRandom();
	}
	
	@Override
	public byte[] getSenderPublicDataRandomKey() throws RemoteException {
		// TODO Auto-generated method stub
		return senderMessageProcessor.getSenderPublicDataRandomKey();
	}

	public void initializeSenderServer() 
	{

		try 
		{
			registry= LocateRegistry.createRegistry( thePort );

			registry.rebind("ExchangePublicData", this);

			senderMessageProcessor.initializeProtocol();

			System.out.println("Server started...");
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public static void main(String [] args) throws Exception
	{
		SenderServerConnection serverConnection = new SenderServerConnection();
		serverConnection.initializeSenderServer();

	}
}
