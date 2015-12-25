package com.vipkid.security;

import java.text.ParseException;
import java.util.UUID;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

public class TokenGenerator {
	private static final String KEY = UUID.randomUUID().toString();
	
	public static String generate() {
		return UUID.randomUUID().toString();
	}
	
	public static String encode(String content) throws JOSEException {
		// Create JWS payload
		Payload payload = new Payload(content);

		// Create JWS header with HS256 algorithm
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
		jwsHeader.setContentType("text/plain");

		// Create JWS object
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);

		// Create HMAC signer
		JWSSigner jwsSigner = new MACSigner(KEY.getBytes());
		jwsObject.sign(jwsSigner);

		// Serialise JWS object to compact format
		return jwsObject.serialize();
	}
	
	public static boolean verify(String content, String token) throws ParseException, JOSEException {
		// Create JWS payload
		Payload payload = new Payload(content);

		// Create JWS header with HS256 algorithm
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
		jwsHeader.setContentType("text/plain");

		// Create JWS object
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);
		
		// Parse back and check signature
		jwsObject = JWSObject.parse(token);

		JWSVerifier jwsVerifier = new MACVerifier(KEY.getBytes());

		return jwsObject.verify(jwsVerifier);
	}
}
