<script setup>
import { computed, ref } from 'vue'
import { submitRefund } from '../api.js'

const props = defineProps({
  selection: { type: Object, required: true },
})

const emit = defineEmits(['close', 'submitted'])

const description = ref('')
const evidence = ref(null)
const error = ref('')
const submitting = ref(false)

const remaining = computed(() => 500 - description.value.length)
const canSubmit = computed(() =>
  description.value.trim().length >= 10 && description.value.length <= 500 && evidence.value && !submitting.value
)

function selectFile(event) {
  evidence.value = event.target.files[0] || null
}

async function send() {
  if (!canSubmit.value) return
  error.value = ''
  submitting.value = true
  try {
    const result = await submitRefund({
      orderId: props.selection.order.id,
      itemId: props.selection.item.id,
      description: description.value,
      evidence: evidence.value,
    })
    emit('submitted', result)
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <section class="modal" role="dialog" aria-modal="true" aria-labelledby="refund-title">
      <button class="close-button" type="button" aria-label="Fermer" @click="emit('close')">×</button>
      <p class="eyebrow">Commande {{ selection.order.id }}</p>
      <h2 id="refund-title">Rembourser {{ selection.item.productName }}</h2>
      <p class="modal-intro">Expliquez le problème rencontré et ajoutez une photo qui nous aidera à traiter votre demande.</p>

      <form @submit.prevent="send">
        <label for="description">Description du problème</label>
        <textarea
          id="description"
          v-model="description"
          maxlength="500"
          rows="5"
          placeholder="Ex. Le produit est arrivé avec une pièce cassée…"
          autofocus
        />
        <div class="field-hint">
          <span>10 caractères minimum</span>
          <span>{{ remaining }} caractères restants</span>
        </div>

        <label for="evidence">Photo justificative</label>
        <label class="file-drop" for="evidence">
          <span class="upload-icon">↑</span>
          <strong>{{ evidence ? evidence.name : 'Choisir une photo' }}</strong>
          <span>JPEG, PNG ou WebP — 5 Mo maximum</span>
        </label>
        <input id="evidence" class="visually-hidden" type="file" accept="image/jpeg,image/png,image/webp" @change="selectFile" />

        <p v-if="error" class="error" role="alert">{{ error }}</p>

        <div class="modal-actions">
          <button class="ghost-button" type="button" @click="emit('close')">Annuler</button>
          <button class="primary-button" type="submit" :disabled="!canSubmit">
            {{ submitting ? 'Envoi…' : 'Envoyer la demande' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

