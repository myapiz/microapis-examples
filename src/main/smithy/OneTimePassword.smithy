namespace com.myapiz.microapis.otp

use smithy.api#Boolean
use smithy.api#String
use smithy.api#required
use alloy#simpleRestJson

@simpleRestJson
service OneTimePasswordService {
    version: "1.0.0"
    operations: [Generate, Validate]
}

@http(method: "POST", uri: "/otp/{id}", code: 200)
operation Generate {
    input: OTPRequest
    output: OTP
}

@http(method: "GET", uri: "/opt/{id}/{code}", code: 200)
operation Validate {
    input: ValidationRequest
    output: ValidationResponse
}

structure OTPRequest {
    @required
    @httpLabel
    id: String

    ttl: Integer
    size: Integer
}

structure OTP {
    @required
    code: String
}

structure ValidationResponse {
    @required
    valid: Boolean
}

structure ValidationRequest {
    @required
    @httpLabel
    id: String

    @required
    @httpLabel
    code: String

}
