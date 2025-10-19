import React, { useEffect, useRef } from 'react';
import { X } from 'lucide-react';

export const Modal: React.FC<{ isOpen: boolean; title: string; onClose: () => void; children: React.ReactNode; width?: number | string }>=({ isOpen, title, onClose, children, width=640 }) => {
  const ref = useRef<HTMLDivElement | null>(null);
  useEffect(() => {
    if (!isOpen) return;
    const prev = document.activeElement as HTMLElement | null;
    ref.current?.focus();
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => { window.removeEventListener('keydown', onKey); prev?.focus?.(); };
  }, [isOpen, onClose]);
  if (!isOpen) return null;
  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" aria-label={title} onClick={onClose}>
      <div className="modal-panel" style={{ width }} onClick={e => e.stopPropagation()} tabIndex={-1} ref={ref}>
        <div className="modal-header">
          <h3>{title}</h3>
          <button className="icon-btn" aria-label="Close" onClick={onClose}><X size={18} /></button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
};
