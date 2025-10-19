import React, { useEffect, useRef } from 'react';

export const Popover: React.FC<{ isOpen: boolean; anchorRef: React.RefObject<Element | null>; onClose: () => void; children: React.ReactNode }>=({ isOpen, anchorRef, onClose, children }) => {
  const popRef = useRef<HTMLDivElement | null>(null);
  useEffect(() => {
    if (!isOpen) return;
    const onClick = (e: MouseEvent) => {
      if (!popRef.current) return;
      if (!popRef.current.contains(e.target as Node) && !anchorRef.current?.contains(e.target as Node)) onClose();
    };
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('mousedown', onClick);
    window.addEventListener('keydown', onKey);
    return () => { window.removeEventListener('mousedown', onClick); window.removeEventListener('keydown', onKey); };
  }, [isOpen, onClose, anchorRef]);
  if (!isOpen) return null;
  return <div className="popover" role="menu" ref={popRef}>{children}</div>;
};


