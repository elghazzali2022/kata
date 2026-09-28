<script setup>
import { ref } from 'vue'
import {
  subscribe,
  getSubscription,
  changePlan
} from './services/subscriptionApi'

const customerId = ref('11111111-1111-1111-1111-111111111111')
const plan = ref('MONTHLY')
const subscription = ref(null)
const error = ref('')
const loading = ref(false)

async function createSubscription() {
  await execute(() => subscribe(customerId.value, plan.value))
}

async function loadSubscription() {
  await execute(() => getSubscription(customerId.value))
}

async function updatePlan() {
  await execute(() => changePlan(customerId.value, plan.value))
}

async function execute(action) {
  loading.value = true
  error.value = ''

  try {
    subscription.value = await action()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="container">
    <h1>Subscription Management</h1>

    <section class="card">
      <label>Customer ID</label>
      <input v-model="customerId" />

      <label>Plan</label>
      <select v-model="plan">
        <option value="MONTHLY">Monthly - €10</option>
        <option value="QUARTERLY">Quarterly - €27</option>
        <option value="ANNUAL">Annual - €96</option>
      </select>

      <div class="actions">
        <button @click="createSubscription" :disabled="loading">
          Subscribe
        </button>

        <button @click="loadSubscription" :disabled="loading">
          Refresh
        </button>

        <button @click="updatePlan" :disabled="loading">
          Change plan
        </button>
      </div>
    </section>

    <p v-if="error" class="error">{{ error }}</p>

    <section v-if="subscription" class="card">
      <h2>Current subscription</h2>
      <p><strong>Plan:</strong> {{ subscription.plan }}</p>
      <p><strong>Start:</strong> {{ subscription.startDate }}</p>
      <p><strong>Next billing:</strong> {{ subscription.nextBillingDate }}</p>
      <p>
        <strong>Next billing adjustment:</strong>
        €{{ subscription.nextBillingAdjustment }}
      </p>
    </section>
  </main>
</template>
