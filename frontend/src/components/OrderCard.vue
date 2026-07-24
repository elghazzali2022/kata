<script setup>
import { computed } from 'vue'

const props = defineProps({
  order: { type: Object, required: true },
})

const emit = defineEmits(['request-refund'])

const formatter = new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' })
const statusLabels = {
  DELIVERED: 'Livrée',
  SHIPPED: 'Expédiée',
  PROCESSING: 'En préparation',
}

const formattedDate = computed(() =>
  new Intl.DateTimeFormat('fr-FR', { dateStyle: 'long' }).format(new Date(props.order.orderedAt))
)

const isRefundable = computed(() => {
  if (props.order.status !== 'DELIVERED' || !props.order.deliveredAt) return false
  const deadline = new Date(props.order.deliveredAt)
  deadline.setDate(deadline.getDate() + 30)
  return deadline >= new Date()
})
</script>

<template>
  <article class="order-card">
    <header class="order-header">
      <div>
        <p class="eyebrow">Commande {{ order.id }}</p>
        <h2>{{ formattedDate }}</h2>
      </div>
      <span class="status" :class="order.status.toLowerCase()">
        {{ statusLabels[order.status] }}
      </span>
    </header>

    <ul class="items">
      <li v-for="item in order.items" :key="item.id" class="item">
        <div class="product-mark" aria-hidden="true">{{ item.productName.charAt(0) }}</div>
        <div class="product-info">
          <strong>{{ item.productName }}</strong>
          <span>Quantité : {{ item.quantity }}</span>
        </div>
        <strong class="price">{{ formatter.format(item.totalPrice) }}</strong>
        <button
          v-if="isRefundable"
          class="secondary-button"
          type="button"
          @click="emit('request-refund', { order, item })"
        >
          Demander un remboursement
        </button>
      </li>
    </ul>

    <footer class="order-footer">
      <span v-if="order.deliveredAt">Livrée le {{ order.deliveredAt }}</span>
      <span v-else>Suivi en cours</span>
      <strong>Total {{ formatter.format(order.totalPrice) }}</strong>
    </footer>
  </article>
</template>

