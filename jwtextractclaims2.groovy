import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

def extractClaimFromJWT(String publicKeyBase64, String privateKeyBase64, String jwtToken, String claimName) {
    // Decode the base64-encoded public key
    def publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64)
    def publicKeySpec = new X509EncodedKeySpec(publicKeyBytes)
    def publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)

    // Decode the base64-encoded private key
    def privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64)
    def privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes)
    def privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)

    // Verify the JWT token and extract the desired claim
    def claimValue = null
    try {
        String[] jwtParts = jwtToken.split("\\.")
        String jwtHeader = new String(Base64.getUrlDecoder().decode(jwtParts[0]), StandardCharsets.UTF_8)
        String jwtPayload = new String(Base64.getUrlDecoder().decode(jwtParts[1]), StandardCharsets.UTF_8)

        // Verify the JWT signature using the public key
        Signature signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update((jwtParts[0] + "." + jwtParts[1]).getBytes(StandardCharsets.UTF_8))
        boolean isSignatureValid = signature.verify(Base64.getUrlDecoder().decode(jwtParts[2]))

        if (isSignatureValid) {
            // Parse the JWT payload
            def jwtPayloadJson = new groovy.json.JsonSlurper().parseText(jwtPayload)
            claimValue = jwtPayloadJson[claimName]
        }
    } catch (Exception ex) {
        // Handle any exception that occurs during JWT processing
        ex.printStackTrace()
    }

    return claimValue
}

// Usage example
def publicKeyBase64 = "YOUR_PUBLIC_KEY_BASE64_ENCODED"
def privateKeyBase64 = "YOUR_PRIVATE_KEY_BASE64_ENCODED"
def jwtToken = "YOUR_JWT_TOKEN"
def claimName = "CLAIM_NAME"

def claimValue = extractClaimFromJWT(publicKeyBase64, privateKeyBase64, jwtToken, claimName)

Response.text(claimValue)
