import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween, uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
  scenarios: {
    average_load: {
      executor: 'constant-arrival-rate',
      rate: Number(__ENV.RATE || 100),
      timeUnit: '1s',
      duration: __ENV.DURATION || '1m',
      preAllocatedVUs: Number(__ENV.VUS || 50),
      maxVUs: Number(__ENV.MAX_VUS || 200),
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const transactionId = uuidv4();
  const amount = randomIntBetween(10, 10000);
  const now = new Date().toISOString();

  const payload = JSON.stringify({
    transactionId,
    accountId: `acc-hash-${randomIntBetween(1, 10000)}`,
    cardId: `card-hash-${randomIntBetween(1, 10000)}`,
    amount,
    currency: 'BRL',
    merchantId: `merchant-${randomIntBetween(1, 1000)}`,
    merchantCategoryCode: '5411',
    channel: 'CARD_PRESENT',
    country: 'BR',
    city: 'SAO_PAULO',
    latitude: -23.5505,
    longitude: -46.6333,
    occurredAt: now,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Trace-Id': uuidv4(),
      'Idempotency-Key': transactionId,
      Authorization: 'Bearer local-dev-token',
    },
  };

  const res = http.post(`${BASE_URL}/v1/transactions`, payload, params);

  check(res, {
    'status is 202 or 409': (r) => r.status === 202 || r.status === 409,
  });

  sleep(0.01);
}
