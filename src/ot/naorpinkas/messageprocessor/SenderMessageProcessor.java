package ot.naorpinkas.messageprocessor;

import java.math.BigInteger;
import java.util.Random;

import ot.naorpinkas.data.SenderMessageProcessingData;
import ot.naorpinkas.data.SenderPublicData;
import ot.naorpinkas.utility.SenderConstant;
import ot.naorpinkas.utility.SenderCryptoUtility;
import ot.naorpinkas.utility.SenderFileUtility;
import ot.naorpinkas.utility.SenderMathUtility;

public class SenderMessageProcessor 
{
	private SenderPublicData senderPublicData = new SenderPublicData();
	private SenderMessageProcessingData senderMessageProcessingData = new SenderMessageProcessingData();

	public SenderMessageProcessor() {
		// TODO Auto-generated constructor stub
	}

	public void initializeProtocol()
	{
		System.out.println("Initialize Protocol Starts");
		
		BigInteger prime = SenderMathUtility.probablePrimeGeneratorForPrimeOrderGroup(SenderConstant.PRIME_CERTAINITY, SenderConstant.PRIME_NUMBER_BIT_LENGTH);

		BigInteger generator = SenderMathUtility.computePrimitiveRoot(prime);

		BigInteger [] randomConstants = generateRandomConstants(prime, generator);

		byte[] randomKey = new byte[SenderConstant.RANDOM_BYTE_STRING_SIZE]; //R
		new Random().nextBytes(randomKey);

		byte [] randomNumber = new byte[SenderConstant.PRIME_NUMBER_BIT_LENGTH]; //r
		new Random().nextBytes(randomNumber);

		BigInteger randomInt = new BigInteger(randomNumber);
		randomInt = randomInt.abs();

		BigInteger generatorWithRandom = SenderMathUtility.pow(generator, randomInt, prime);

		senderPublicData.setConstant(randomConstants);
		senderPublicData.setGenerator(generator);
		senderPublicData.setGeneratorWithRandom(generatorWithRandom);
		senderPublicData.setPrime(prime);
		senderPublicData.setRandomKey(randomKey);

		senderMessageProcessingData.setRandom(randomInt);
		
		System.out.println("Initialize Protocol Ends");
	}

	public SenderPublicData sendPublicDataToChooser()
	{
		return senderPublicData;
	}
	
//	public List<byte []>  sendEncryptedMessageChainToChooser(BigInteger pk)
//	{
//		BigInteger prime = senderPublicData.getPrime();
//		BigInteger divisor = SenderMathUtility.pow(pk, senderMessageProcessingData.getRandom(), prime);
//		BigInteger modInverse = SenderMathUtility.modInversePrime(divisor, prime);
//		BigInteger [] randomConstants = senderPublicData.getConstant();
//		
//		List<byte []> cryptoMessage = new ArrayList<byte []>();
//		
//		byte[] b = new byte[SenderConstant.RANDOM_BYTE_STRING_SIZE]; //R
//		new Random().nextBytes(b);
//		cryptoMessage.add(b);
//		
//		for(int i =1 ; i< SenderConstant.TOTAL_CHOOSERS; i++)
//		{
//			BigInteger multiplier = SenderMathUtility.pow(randomConstants[i], senderMessageProcessingData.getRandom(), prime);
//			BigInteger rawData = (multiplier.multiply(modInverse)).mod(prime);
//			byte [] hashData = SenderCryptoUtility.generateHashData(senderPublicData.getRandomKey(), rawData.toByteArray());
//			byte [] message = SenderFileUtility.readByteFromFile("Messages/User"+Integer.toString(i)+".txt");
//			byte[] encryptMessage = SenderCryptoUtility.encryptMessage(hashData, message);
//			cryptoMessage.add(encryptMessage);
//		}
//		
//		return cryptoMessage;
//	}
	
	public byte[][]  sendEncryptedMessageChainToChooser(BigInteger pk) throws Exception
	{
		System.out.println("sendEncryptedMessageChainToChooser Starts");
		BigInteger prime = senderPublicData.getPrime();
		BigInteger divisor = SenderMathUtility.pow(pk, senderMessageProcessingData.getRandom(), prime);
		BigInteger modInverse = SenderMathUtility.modInversePrime(divisor, prime);
		BigInteger [] randomConstants = senderPublicData.getConstant();
		
		byte[][] cryptoMessage;
		cryptoMessage = new byte[SenderConstant.TOTAL_CHOOSERS][];
		
		BigInteger rawData = SenderMathUtility.pow(pk, senderMessageProcessingData.getRandom(), prime);
		byte [] hashData = SenderCryptoUtility.generateHashData(senderPublicData.getRandomKey(), rawData.toByteArray());
		byte [] message = SenderFileUtility.readByteFromFile("Messages/User"+Integer.toString(0)+".txt");
		byte[] encryptMessage = SenderCryptoUtility.encryptMessage(hashData, message);
		cryptoMessage[0] = encryptMessage;
		
		for(int i =0 ; i< SenderConstant.TOTAL_CHOOSERS-1; i++)
		{
			BigInteger multiplier = SenderMathUtility.pow(randomConstants[i], senderMessageProcessingData.getRandom(), prime);
			rawData = (multiplier.multiply(modInverse)).mod(prime);
			hashData = SenderCryptoUtility.generateHashData(senderPublicData.getRandomKey(), rawData.toByteArray());
			message = SenderFileUtility.readByteFromFile("Messages/User"+Integer.toString(i+1)+".txt");
			encryptMessage = SenderCryptoUtility.encryptMessage(hashData, message);
			cryptoMessage[i+1] = encryptMessage;

		}
		System.out.println("sendEncryptedMessageChainToChooser Ends");
		return cryptoMessage;
	}

	public BigInteger[] generateRandomConstants(BigInteger prime, BigInteger generator )
	{
		System.out.println("generateRandomConstants Starts");
		int numberOfConstants = SenderConstant.TOTAL_CHOOSERS-1;
		BigInteger [] randomConstants = new BigInteger[numberOfConstants];

		for(int i =0; i< numberOfConstants; i++)
		{
			byte[] b = new byte[SenderConstant.PRIME_NUMBER_BIT_LENGTH-1];
			new Random().nextBytes(b);
			BigInteger bigInteger = new BigInteger(b);
			bigInteger = bigInteger.abs();
			randomConstants[i] = SenderMathUtility.pow(generator, bigInteger,prime);
		}

		System.out.println("generateRandomConstants Ends");
		
		return randomConstants;
	}
	
	public BigInteger getSenderPublicDataGenerator() {
		return senderPublicData.getGenerator();
	}
	public BigInteger[] getSenderPublicDataConstant() {
		return senderPublicData.getConstant();
	}

	public byte[] getSenderPublicDataRandomKey() {
		return senderPublicData.getRandomKey();
	}


	public BigInteger getSenderPublicDataGeneratorWithRandom() {
		return senderPublicData.getGeneratorWithRandom();
	}

	public BigInteger getSenderPublicDataPrime() {
		return senderPublicData.getPrime();
	}


}
