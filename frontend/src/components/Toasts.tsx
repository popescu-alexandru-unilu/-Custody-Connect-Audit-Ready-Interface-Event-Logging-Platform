import React from 'react';
import { X } from 'lucide-react';

export type ToastItem = { id: string; text: string };

export const Toasts: React.FC<{ items: ToastItem[]; onDismiss: (id: string) => void }>=({ items, onDismiss }) => (
  <div className="toast-region" role="region" aria-live="polite" aria-label="Notifications">
    {items.map(t => (
      <div key={t.id} className="toast">
        <span>{t.text}</span>
        <button className="icon-btn" aria-label="Dismiss" onClick={() => onDismiss(t.id)}><X size={14} /></button>
      </div>
    ))}
  </div>
);
