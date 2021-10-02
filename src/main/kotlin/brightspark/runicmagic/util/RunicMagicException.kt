package brightspark.runicmagic.util

class RunicMagicException : RuntimeException {
	constructor(message: String) : super(message)
	constructor(message: String, cause: Throwable) : super(message, cause)
}
