// TypeScript client for the Multi-Tenant Notification Service (Project 3).
// Subscribes to the tenant's STOMP topic so UI state updates live when the
// backend publishes a notification — no polling.

import { Client, type IMessage } from "@stomp/stompjs";

export interface Notification {
  id: string;
  tenantId: string;
  userId: string;
  type: string;
  message: string;
  read: boolean;
  createdAt: string;
}

const REST_BASE = "http://localhost:8083/api/notifications";
const WS_URL = "ws://localhost:8083/ws";

/** Fetch the backlog for a user within one tenant. */
export async function fetchNotifications(tenantId: string, userId: string): Promise<Notification[]> {
  const res = await fetch(`${REST_BASE}?userId=${encodeURIComponent(userId)}`, {
    headers: { "X-Tenant-Id": tenantId },
  });
  if (!res.ok) throw new Error(`fetch failed: ${res.status}`);
  return res.json();
}

/** Publish a notification (e.g. from a workflow step in the UI). */
export async function sendNotification(
  tenantId: string,
  userId: string,
  type: string,
  message: string
): Promise<Notification> {
  const res = await fetch(REST_BASE, {
    method: "POST",
    headers: { "X-Tenant-Id": tenantId, "Content-Type": "application/json" },
    body: JSON.stringify({ userId, type, message }),
  });
  if (!res.ok) throw new Error(`send failed: ${res.status}`);
  return res.json();
}

/**
 * Subscribe to live notifications for a tenant. The callback fires for every
 * notification published to that tenant while connected. Returns a function
 * that disconnects.
 */
export function subscribeLive(
  tenantId: string,
  onNotification: (n: Notification) => void
): () => void {
  const client = new Client({
    brokerURL: WS_URL,
    reconnectDelay: 2000,
    onConnect: () => {
      client.subscribe(`/topic/tenant-${tenantId}`, (msg: IMessage) => {
        onNotification(JSON.parse(msg.body) as Notification);
      });
    },
  });
  client.activate();
  return () => {
    void client.deactivate();
  };
}
