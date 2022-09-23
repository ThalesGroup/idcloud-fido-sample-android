package com.thalesgroup.gemalto.idcloud.auth.sample;

public class Configuration {

    // IdCloud Mobile Service Server URL
    public final static String url = "";

    //IdCloud Service Tenant Id
    public final static String tenantId = "";

    //SafetyNetAttestationKey. Get new key from here: https://developer.android.com/training/safetynet/attestation#obtain-api-key, and put below.
    public final static String safetyNetAttestationKey = "";

    // SecureLog public key modulus. Used to encrypt the log.
    public final static byte[] secureLogPublicKeyModulus = new byte[] {};
    // SecureLog public key exponent. Used to encrypt the log.
    public final static byte[] secureLogPublicKeyExponent = new byte[] {};

    // IdCloud PIN Authenticator rules
    public final static int pinRules = PinConfig.PinRule.LENGTH
            | PinConfig.PinRule.PALINDROME
            | PinConfig.PinRule.SERIES
            | PinConfig.PinRule.UNIFORM;

    // IdCloud PIN Authenticator minimum and maximum lengths
    public final static int[] pinLength = new int[]{6, 8};

}