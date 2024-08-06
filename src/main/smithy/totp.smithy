$version: "2"

namespace com.myapiz.microapis.otp

use smithy.api#required
use alloy#simpleRestJson

use com.myapiz.smithy.error#NotFoundError
use com.myapiz.smithy.error#NotAuthenticatedError
use com.myapiz.smithy.error#NotAuthorizedError
use com.myapiz.smithy.auth#authorization

string Code
string ID


@simpleRestJson
@httpApiKeyAuth(name: "X-myapiz-user", in: "header")
service TOTP {
    version: "1.0.0"
    operations: [Generate, Validate]
    errors: [NotAuthorizedError, NotAuthenticatedError, NotFoundError]
}

@http(method: "POST", uri: "/totp/{id}", code: 200)
@authorization(allow: ["write"])
operation Generate {
    input: GenerateRequest
    output: OTP
}

@readonly
@http(method: "GET", uri: "/totp/{id}/{code}", code: 200)
@authorization(allow: ["read"])
operation Validate {
    input: ValidationRequest
    output: ValidationResponse
}

structure GenerateRequest {
    @required
    @httpLabel
    id: ID

    ttl: Integer = 60
    size: Integer = 6
}

structure OTP {
    @required
    code: Code
}

structure ValidationRequest {
    @required
    @httpLabel
    id: ID

    @required
    @httpLabel
    code: Code

    @httpQuery("ttl")
    ttl: Integer = 60

}

structure ValidationResponse {
    @required
    valid: Boolean
}

