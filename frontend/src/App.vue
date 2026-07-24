<script setup>
import { onMounted, ref } from 'vue'
import { getOrders } from './api.js'
import OrderCard from './components/OrderCard.vue'
import RefundModal from './components/RefundModal.vue'

const orders = ref([])
const loading = ref(true)
const error = ref('')
const selection = ref(null)
const confirmation = ref('')

onMounted(async () => {
  try {
    orders.value = await getOrders()
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
})

function openRefund(payload) {
  confirmation.value = ''
  selection.value = payload
}

function refundSubmitted(request) {
  selection.value = null
  confirmation.value = `Votre demande ${request.id.slice(0, 8)} a bien été enregistrée.`
}
</script>

<template>
  <header class="topbar">
    <a class="brand" href="/" aria-label="Accueil">
      <span class="brand-symbol">C</span>
      <span>Carrefour</span>
    </a>
    <div class="profile">
      <span class="avatar">ME</span>
      <span>Mon compte</span>
    </div>
  </header>

  <main>
    <section class="hero">
      <p class="eyebrow">Espace client</p>
      <h1>Mes commandes</h1>
      <p>Retrouvez vos achats récents et signalez simplement un problème.</p>
    </section>

    <div v-if="confirmation" class="success" role="status">
      <span>✓</span>
      {{ confirmation }}
    </div>
    <p v-if="loading" class="state-message">Chargement de vos commandes…</p>
    <p v-else-if="error" class="state-message error">{{ error }}</p>
    <section v-else class="order-list" aria-label="Historique des commandes">
      <OrderCard
        v-for="order in orders"
        :key="order.id"
        :order="order"
        @request-refund="openRefund"
      />
    </section>
  </main>

  <RefundModal
    v-if="selection"
    :selection="selection"
    @close="selection = null"
    @submitted="refundSubmitted"
  />
</template>

