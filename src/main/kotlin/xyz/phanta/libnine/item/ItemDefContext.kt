package xyz.phanta.libnine.item

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import xyz.phanta.libnine.definition.DefBody
import xyz.phanta.libnine.definition.Registrar
import xyz.phanta.libnine.util.snakeify
import kotlin.reflect.KMutableProperty0

typealias ItemPrimer = (ItemDefBuilder<*>) -> ItemDefBuilder<*>

interface ItemDefContextBase {

    val registrar: Registrar

    fun <I : Item> item(
            dest: KMutableProperty0<in I>,
            factory: (Item.Properties) -> I,
            body: (ItemDefBuilder<I>) -> I = { it.build() }
    )

    fun <I : Item> itemsAug(
            factory: (Item.Properties) -> I,
            primer: ItemPrimer = { it },
            body: DefBody<ItemDefContextAugmented<I>>
    ) = body(MappingItemDefContextAugmented(registrar, this, factory, primer))

    fun <I : Item> createItemBuilder(name: ResourceLocation, factory: (Item.Properties) -> I): ItemDefBuilder<I>

}

interface ItemDefContext : ItemDefContextBase {

    fun itemsBy(primer: ItemPrimer, body: DefBody<ItemDefContext>) =
            body(MappingItemDefContext(registrar, this, primer))

}

interface ItemDefContextAugmented<A : Item> : ItemDefContextBase {

    fun item(dest: KMutableProperty0<in A>, body: (ItemDefBuilder<A>) -> A = { it.build() })

    fun itemsBy(primer: ItemPrimer, body: DefBody<ItemDefContextAugmented<A>>)

}

internal open class ItemDefContextBaseImpl(override val registrar: Registrar) : ItemDefContextBase {

    override fun <I : Item> item(dest: KMutableProperty0<in I>, factory: (Item.Properties) -> I, body: (ItemDefBuilder<I>) -> I) {
        val item = body(createItemBuilder(registrar.mod.resource(dest.name.snakeify()), factory))
        registrar.items += item
        dest.set(item)
    }

    override fun <I : Item> createItemBuilder(name: ResourceLocation, factory: (Item.Properties) -> I): ItemDefBuilder<I> =
            ItemDefBuilderImpl(name, factory)

}

private open class MappingItemDefContextBase(
        registrar: Registrar,
        private val parent: ItemDefContextBase,
        private val primer: ItemPrimer
) : ItemDefContextBaseImpl(registrar) {

    @Suppress("UNCHECKED_CAST")
    override fun <I : Item> createItemBuilder(name: ResourceLocation, factory: (Item.Properties) -> I): ItemDefBuilder<I> =
            primer(parent.createItemBuilder(name, factory)) as ItemDefBuilder<I>

}

private class MappingItemDefContext(
        registrar: Registrar,
        parent: ItemDefContextBase,
        primer: ItemPrimer
) : MappingItemDefContextBase(registrar, parent, primer), ItemDefContext

private class MappingItemDefContextAugmented<A : Item>(
        registrar: Registrar,
        parent: ItemDefContextBase,
        private val factory: (Item.Properties) -> A,
        primer: ItemPrimer
) : MappingItemDefContextBase(registrar, parent, primer), ItemDefContextAugmented<A> {

    override fun item(dest: KMutableProperty0<in A>, body: (ItemDefBuilder<A>) -> A) {
        val item = body(createItemBuilder(registrar.mod.resource(dest.name.snakeify()), factory))
        registrar.items += item
        dest.set(item)
    }

    override fun itemsBy(primer: ItemPrimer, body: DefBody<ItemDefContextAugmented<A>>) =
            body(MappingItemDefContextAugmented(registrar, this, factory, primer))

}
