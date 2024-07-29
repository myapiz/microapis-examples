namespace com.myapiz.microapis.otp

use smithy.api#Boolean
use smithy.api#String
use smithy.api#required
use alloy#simpleRestJson

string Code
string ID

@simpleRestJson
service Service {
    version: "1.0.0"
    operations: [Generate, Validate]
}

@http(method: "POST", uri: "/totp/{id}", code: 200)
operation Generate {
    input: GenerateRequest
    output: OTP
}

@readonly
@http(method: "GET", uri: "/totp/{id}/{code}", code: 200)
operation Validate {
    input: ValidationRequest
    output: ValidationResponse
}

structure GenerateRequest {
    @required
    @httpLabel
    id: ID

    ttl: Integer
    size: Integer
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
    ttl: Integer

}

structure ValidationResponse {
    @required
    valid: Boolean
}

