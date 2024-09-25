$version: "2"

namespace com.myapiz.microapis.otp

use smithy.api#required
use alloy#simpleRestJson

use com.myapiz.smithy.error#NotFoundError
use com.myapiz.smithy.error#NotAuthenticatedError
use com.myapiz.smithy.error#NotAuthorizedError
use com.myapiz.smithy.auth#authorization

@documentation("Time-Based One-Time Password Code or Password")
string Code

@documentation("TOTP Resource Identifier")
string ID


@simpleRestJson
@httpApiKeyAuth(name: "X-myapiz-user", in: "header")
@documentation("This service allows for generating and validating time-based one-time passwords that can be used for security or other purposes.")
@title("Time-Based One-Time Password")
service TOTP {
    version: "1.0.0"
    operations: [Generate, Validate]
    errors: [NotAuthorizedError, NotAuthenticatedError, NotFoundError]
}

@http(method: "POST", uri: "/totp/{id}", code: 200)
@authorization(allow: ["write"])
@documentation("Generate a new time-based one-time password for the given resource identifier. The generated code is valid for a specified time-to-live (TTL) in seconds.")
@tags(["security"])
operation Generate {
    input: GenerateInput
    output: GenerateOutput
}

@readonly
@http(method: "GET", uri: "/totp/{id}/{code}", code: 200)
@authorization(allow: ["read"])
@documentation("Validates a code created with the given ID. If the code was created with the same ID and within the TTL seconds of its creation then it is considered valid. The validation returns a boolean value indicating whether the code is valid or not.")
@tags(["security"])
operation Validate {
    input: ValidationInput
    output: ValidationOutput
}

structure GenerateInput {
    @required
    @httpLabel
    id: ID

    @documentation("Time-to-live (TTL) in seconds. The generated code is valid for this time-to-live. Default is 60 seconds.")
    ttl: Integer = 60
    @documentation("Size of the generated code. Default is 6.")
    size: Integer = 6
}

structure GenerateOutput {
    @required
    code: Code
}

structure ValidationInput {
    @required
    @httpLabel
    id: ID

    @required
    @httpLabel
    code: Code

    @httpQuery("ttl")
    ttl: Integer = 60

}

structure ValidationOutput {
    @required
    valid: Boolean
}


apply Generate
@examples([
    {
        title: "Generate a new code",
        documentation: "Generate a new code for the given ID and TTL."
        input: {
            id: "code-user-myapiz",
            size: 6,
            ttl: 60
        }
        output: {
            code: "123456"
        }
    }
])


