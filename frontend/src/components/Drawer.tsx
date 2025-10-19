import React, { useEffect } from 'react';
import { X } from 'lucide-react';

export const Drawer: React.FC<{ isOpen: boolean; title: string; onClose: () => void; children: React.ReactNode; side?: 'right'|'left' }>=({ isOpen, title, onClose, children, side='right' }) => {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    if (isOpen) window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [isOpen, onClose]);
  if (!isOpen) return null;
  return (
    <div className="drawer-backdrop" onClick={onClose}>
      <aside className={`drawer-panel ${side}`} onClick={e => e.stopPropagation()} role="dialog" aria-modal="true">
        <div className="drawer-header">
          <h3>{title}</h3>
          <button className="icon-btn" aria-label="Close" onClick={onClose}><X size={18} /></button>
        </div>
        <div className="drawer-body">{children}</div>
      </aside>
    </div>
  );
};
