const API_URL = 'http://localhost:8080/api/subscriptions'

export async function subscribe(customerId, plan) {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ customerId, plan })
  })

  if (!response.ok) {
    throw new Error(await response.text())
  }

  return response.json()
}

export async function getSubscription(customerId) {
  const response = await fetch(`${API_URL}/${customerId}`)

  if (!response.ok) {
    throw new Error(await response.text())
  }

  return response.json()
}

export async function changePlan(customerId, plan) {
  const response = await fetch(`${API_URL}/${customerId}/plan`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ plan })
  })

  if (!response.ok) {
    throw new Error(await response.text())
  }

  return response.json()
}
