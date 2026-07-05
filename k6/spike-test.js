import http from 'k6/http';
import { check } from 'k6';
import { randomIntBetween, uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
  stages: [
    { duration: '30s', target: 100 },
    { duration: '30s', target: 1000 },
    { duration: '1m', target: 1000 },
    { duration: '30s', target: 100 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<750', 'p(99)<1500'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const transactionId = uuidv4();

  const payload = JSON.stringify({
    transactionId,
    accountId: `acc-hash-${randomIntBetween(1, 5000)}`,
    cardId: `card-hash-${randomIntBetween(1, 5000)}`,
    amount: randomIntBetween(10, 15000),
    currency: 'BRL',
    merchantId: `merchant-${randomIntBetween(1, 500)}`,
    merchantCategoryCode: '5411',
    channel: 'CARD_PRESENT',
    country: 'BR',
    city: 'SAO_PAULO',
    latitude: -23.5505,
    longitude: -46.6333,
    occurredAt: new Date().toISOString(),
  });

  const res = http.post(`${BASE_URL}/v1/transactions`, payload, {
    headers: {
      'Content-Type': 'application/json',
      'X-Trace-Id': uuidv4(),
      'Idempotency-Key': transactionId,
      Authorization: 'Bearer local-dev-token',
    },
  });

  check(res, {
    'accepted or duplicated': (r) => r.status === 202 || r.status === 409,
  });
}
