async function parseResponse(response) {
  if (response.ok) return response.json()

  let message = 'Une erreur est survenue.'
  try {
    const problem = await response.json()
    message = problem.detail || message
  } catch {
    // The fallback message is enough when the server response is not JSON.
  }
  throw new Error(message)
}

export async function getOrders() {
  return parseResponse(await fetch('/api/orders'))
}

export async function submitRefund({ orderId, itemId, description, evidence }) {
  const form = new FormData()
  form.append('orderId', orderId)
  form.append('itemId', itemId)
  form.append('description', description)
  form.append('evidence', evidence)

  return parseResponse(await fetch('/api/refunds', {
    method: 'POST',
    body: form,
  }))
}

