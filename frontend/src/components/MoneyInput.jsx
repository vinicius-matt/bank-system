/**
 * Input de valor com máscara de moeda (BRL).
 *
 * - Trabalha em centavos: o usuário digita só números e o componente formata
 *   na hora (ex.: digitar 12345 vira "1.234,45").
 * - `value` é um Number em reais (ex.: 1234.45) e `onChange` devolve um Number.
 * - Impede caracteres inválidos, pois ignora tudo que não for dígito.
 */
const formatar = (reais) =>
  (Number.isFinite(reais) ? reais : 0).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })

export default function MoneyInput({
  value,
  onChange,
  placeholder = '0,00',
  autoFocus = false,
  className = 'input',
  id,
  prefix = 'R$',
}) {
  const handleChange = (e) => {
    const digits = e.target.value.replace(/\D/g, '')
    const reais = digits ? Number(digits) / 100 : 0
    onChange(reais)
  }

  const handleKeyDown = (e) => {
    // Atalho: Backspace já é tratado pelo replace; bloqueia ',' e '.' soltos
    if (e.key === ',' || e.key === '.') e.preventDefault()
  }

  const display = value ? formatar(value) : ''

  return (
    <div className="input-prefix">
      <span>{prefix}</span>
      <input
        id={id}
        className={className}
        type="text"
        inputMode="numeric"
        autoComplete="off"
        placeholder={placeholder}
        autoFocus={autoFocus}
        value={display}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
      />
    </div>
  )
}
