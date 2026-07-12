<div
	class="overlay"
	role="status"
	aria-live="polite"
	aria-label="Cargando contenido"
>
	<div class="spinner"></div>
</div>

<style>
	.overlay {
		position: fixed;
		z-index: 45;
		inset: 82px 0 0;
		background: #f8f7f3;
		display: grid;
		place-content: center;
	}
	.spinner {
		width: 44px;
		height: 44px;
		border: 3px solid rgba(25, 26, 23, 0.14);
		border-top-color: var(--ink);
		border-radius: 50%;
		animation: spin 0.75s linear infinite;
	}
	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}
	@media (prefers-reduced-motion: reduce) {
		.spinner {
			animation-duration: 1.8s;
		}
	}
</style>
