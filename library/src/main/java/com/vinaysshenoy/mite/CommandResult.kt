package com.vinaysshenoy.mite

sealed class CommandResult<D> {

	data class Pass<D>(val data: D?) : CommandResult<D>()

	data class SoftFail<D>(val data: D?) : CommandResult<D>()

	data class HardFail<D>(val data: D?) : CommandResult<D>()

	companion object {

		@JvmStatic
		fun <P> pass(data: P? = null): Pass<P> = Pass(data)

		@JvmStatic
		fun <S> softFail(data: S? = null): SoftFail<S> = SoftFail(data)

		@JvmStatic
		fun <H> hardFail(data: H? = null): HardFail<H> = HardFail(data)
	}
}
