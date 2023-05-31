import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date

/**
 * Generate and return a signed JWT using custom-defined public and private keys.
 *
 * @param publicKeyBase64  Base64-encoded public key
 * @param privateKeyBase64 Base64-encoded private key
 * @param claims           JWT claims as a Map
 * @param expiryTime       Expiration time in seconds
 * @return Signed JWT
 */
def generateSignedJWT(String publicKeyBase64, String privateKeyBase64, Map<String, Object> claims, int expiryTime) {
    // Decode the Base64-encoded keys
    def publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64)
    def privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64)

    // Load the public key
    def publicKey = loadPublicKey(publicKeyBytes)

    // Load the private key
    def privateKey = loadPrivateKey(privateKeyBytes)

    // Define the JWT header
    def header = [
            "alg": "RS256",
            "typ": "JWT"
    ]

    // Encode the JWT header as JSON
    def headerJson = new groovy.json.JsonBuilder(header).toString()

    // Encode the claims as JSON
    def claimsJson = new groovy.json.JsonBuilder(claims).toString()

    // Encode the JWT header and payload
    def headerAndPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes()) +
            "." +
            Base64.getUrlEncoder().withoutPadding().encodeToString(claimsJson.getBytes())

    // Sign the JWT using the private key
    def signature = signWithRSA(headerAndPayload, privateKey)

    // Combine the JWT header, payload, and signature
    def signedJWT = headerAndPayload +
            "." +
            Base64.getUrlEncoder().withoutPadding().encodeToString(signature)

    return signedJWT
}

/**
 * Load the public key from a byte array.
 *
 * @param publicKeyBytes Byte array representing the public key
 * @return Public key object
 */
def loadPublicKey(byte[] publicKeyBytes) {
    def keySpec = new X509EncodedKeySpec(publicKeyBytes)
    def keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}

/**
 * Load the private key from a byte array.
 *
 * @param privateKeyBytes Byte array representing the private key
 * @return Private key object
 */
def loadPrivateKey(byte[] privateKeyBytes) {
    def keySpec = new PKCS8EncodedKeySpec(privateKeyBytes)
    def keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePrivate(keySpec)
}

/**
 * Sign the input data using RSA private key.
 *
 * @param data       Data to be signed
 * @param privateKey RSA private key
 * @return Signature bytes
 */
def signWithRSA(String data, PrivateKey privateKey) {
    def signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(privateKey)
    signature.update(data.getBytes())
    return signature.sign()
}

// Usage example:

// Define your custom public and private keys in Base64-encoded format
def publicKeyBase64 = '<YOUR_PUBLIC_KEY>'
def privateKeyBase64 = '<YOUR_PRIVATE_KEY>'

// Define the claims for the JWT
def claims = [
    "sub": "example_user",
    "iss": "issuer",
    "exp": new Date().getTime() / 1000 + 3600 // Set expiration time to 1 hour from now
]

// Define the expiration time for the JWT in seconds
def expiryTime = 3600

// Generate the signed JWT
def signedJWT = generateSignedJWT(publicKeyBase64, privateKeyBase64, claims, expiryTime)

println("Generated JWT: $signedJWT")
