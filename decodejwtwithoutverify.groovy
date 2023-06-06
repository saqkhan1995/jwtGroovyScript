def extractClaimFromJWT(String jwt, String claimName) {
    def parts = jwt.split("\\.")
    def encodedPayload = parts[1]
    def decodedPayload = new String(Base64.getUrlDecoder().decode(encodedPayload), "UTF-8")

    def jsonPayload = new groovy.json.JsonSlurper().parseText(decodedPayload)
    def claimValue = jsonPayload[claimName]

    return claimValue
}

// Example usage
def jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwieC1uYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
def claimName = "x-name"

def extractedClaim = extractClaimFromJWT(jwt, claimName)
println(extractedClaim)
