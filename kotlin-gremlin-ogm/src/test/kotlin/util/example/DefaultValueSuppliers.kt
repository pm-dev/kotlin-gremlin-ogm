package util.example

import java.util.function.Supplier

class DefaultStringSupplier : Supplier<String> {
    override fun get() = "DefaultString"
}
