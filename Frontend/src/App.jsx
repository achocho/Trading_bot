import { useEffect, useState } from "react";

async function apiGetJson(path) {
  const r = await fetch(path);
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

async function apiGetText(path) {
  const r = await fetch(path);
  if (!r.ok) throw new Error(await r.text());
  return r.text();
}

async function apiPost(path) {
  const r = await fetch(path, { method: "POST" });
  if (!r.ok) throw new Error(await r.text());
  return r.text();
}

export default function App() {
  const [portfolio, setPortfolio] = useState(null);
  const [trades, setTrades] = useState([]);
  const [status, setStatus] = useState("");
  const [trainResult, setTrainResult] = useState(null);
  const [error, setError] = useState("");


  const [symbol, setSymbol] = useState("BTCUSDT");
  const [timeframe, setTimeframe] = useState("1m");
  const [limit, setLimit] = useState(200);

  const [fast, setFast] = useState(10);
  const [slow, setSlow] = useState(30);

  async function refresh() {
    try {
      setError("");
      const [p, t, s] = await Promise.all([
        apiGetJson("/api/portfolio"),
        apiGetJson("/api/trades"),
        apiGetText("/api/bot/status"),
      ]);
      setPortfolio(p);
      setTrades(t);
      setStatus(s);
    } catch (e) {
      setError(String(e.message || e));
    }
  }

  useEffect(() => {
    refresh();
    const timerId = window.setInterval(() => refresh(), 2000);
    return () => window.clearInterval(timerId);
  }, []);

  async function applyParams() {
    setTrainResult(null);
    await apiPost(
      `/api/bot/params?symbol=${encodeURIComponent(symbol)}&interval=${encodeURIComponent(
        timeframe
      )}&limit=${limit}`
    );
    await refresh();
  }

  async function start() {
    await apiPost("/api/bot/start");
    await refresh();
  }

  async function pause() {
    await apiPost("/api/bot/pause");
    await refresh();
  }

  async function reset() {
    setTrainResult(null);
    await apiPost("/api/bot/reset");
    await refresh();
  }

  async function train() {
    setTrainResult(null);
    setError("");
    try {
     
      const res = await apiGetJson(
        `/api/bot/train?symbol=${encodeURIComponent(symbol)}&interval=${encodeURIComponent(
          timeframe
        )}&limit=${limit}&fast=${fast}&slow=${slow}`
      );
      setTrainResult(res);
      await refresh();
    } catch (e) {
      setError(String(e.message || e));
    }
  }

  const card = {
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    padding: 14,
    background: "white",
  };

  const btn = (primary = false) => ({
    padding: "8px 12px",
    borderRadius: 10,
    border: primary ? "1px solid #111827" : "1px solid #d1d5db",
    background: primary ? "#111827" : "white",
    color: primary ? "white" : "#111827",
    cursor: "pointer",
    fontWeight: 600,
  });

  return (
    <div
      style={{
        fontFamily: "system-ui",
        background: "#f8fafc",
        minHeight: "100vh",
        padding: 16,
      }}
    >
      <div style={{ maxWidth: 1100, margin: "0 auto" }}>
        <h1 style={{ marginBottom: 6 }}>Trading Bot Dashboard</h1>
        <div style={{ opacity: 0.75, marginBottom: 12 }}>Status: {status}</div>

        {error && (
          <div
            style={{
              ...card,
              borderColor: "#fecaca",
              background: "#fff1f2",
              color: "#991b1b",
              marginBottom: 12,
            }}
          >
            <b>Error:</b> {error}
          </div>
        )}

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          <div style={card}>
            <h3 style={{ marginTop: 0 }}>Bot Controls</h3>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr 1fr",
                gap: 8,
                marginBottom: 10,
              }}
            >
              <div>
                <div style={{ fontSize: 12, opacity: 0.7 }}>Symbol</div>
                <input
                  value={symbol}
                  onChange={(e) => setSymbol(e.target.value)}
                  style={{ width: "100%", padding: 8, borderRadius: 10, border: "1px solid #d1d5db" }}
                />
              </div>

              <div>
                <div style={{ fontSize: 12, opacity: 0.7 }}>Interval</div>
                <input
                  value={timeframe}
                  onChange={(e) => setTimeframe(e.target.value)}
                  style={{ width: "100%", padding: 8, borderRadius: 10, border: "1px solid #d1d5db" }}
                />
              </div>

              <div>
                <div style={{ fontSize: 12, opacity: 0.7 }}>Limit</div>
                <input
                  value={limit}
                  type="number"
                  onChange={(e) => setLimit(Number(e.target.value))}
                  style={{ width: "100%", padding: 8, borderRadius: 10, border: "1px solid #d1d5db" }}
                />
              </div>
            </div>

            <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
              <button style={btn()} onClick={applyParams}>
                Apply Params
              </button>
              <button style={btn(true)} onClick={start}>
                Start
              </button>
              <button style={btn()} onClick={pause}>
                Pause
              </button>
              <button style={btn()} onClick={reset}>
                Reset
              </button>
            </div>

            <hr style={{ margin: "14px 0", border: "none", borderTop: "1px solid #e5e7eb" }} />

            <h3 style={{ marginTop: 0 }}>Training (Backtest)</h3>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginBottom: 10 }}>
              <div>
                <div style={{ fontSize: 12, opacity: 0.7 }}>Fast MA</div>
                <input
                  value={fast}
                  type="number"
                  onChange={(e) => setFast(Number(e.target.value))}
                  style={{ width: "100%", padding: 8, borderRadius: 10, border: "1px solid #d1d5db" }}
                />
              </div>
              <div>
                <div style={{ fontSize: 12, opacity: 0.7 }}>Slow MA</div>
                <input
                  value={slow}
                  type="number"
                  onChange={(e) => setSlow(Number(e.target.value))}
                  style={{ width: "100%", padding: 8, borderRadius: 10, border: "1px solid #d1d5db" }}
                />
              </div>
            </div>

            <button style={btn(true)} onClick={train}>
              Run Training (reset + backtest)
            </button>

            {trainResult && (
              <div style={{ marginTop: 12, padding: 10, borderRadius: 10, background: "#f1f5f9" }}>
                <div><b>Trades:</b> {trainResult.trades}</div>
                <div><b>Final cash:</b> {trainResult.finalCash}</div>
                <div><b>Final equity:</b> {trainResult.finalEquity}</div>
                <div><b>Return %:</b> {trainResult.returnPct}</div>
              </div>
            )}
          </div>

          <div style={card}>
            <h3 style={{ marginTop: 0 }}>Portfolio</h3>
            {!portfolio ? (
              <div>Loading...</div>
            ) : (
              <>
                <div style={{ fontSize: 16, marginBottom: 10 }}>
                  <b>Cash:</b> {portfolio.cash}
                </div>

                <div style={{ fontWeight: 700, marginBottom: 6 }}>Holdings</div>
                {portfolio.holdings.length === 0 ? (
                  <div style={{ opacity: 0.7 }}>(none)</div>
                ) : (
                  <ul style={{ margin: 0, paddingLeft: 18 }}>
                    {portfolio.holdings.map((h) => (
                      <li key={h.symbol}>
                        <b>{h.symbol}</b> — qty: {h.qty} — avg: {h.avgPrice}
                      </li>
                    ))}
                  </ul>
                )}
              </>
            )}
          </div>
        </div>

        <div style={{ ...card, marginTop: 12 }}>
          <h3 style={{ marginTop: 0 }}>Trades</h3>
          <div style={{ overflowX: "auto" }}>
            <table width="100%" cellPadding="8" style={{ borderCollapse: "collapse" }}>
              <thead style={{ background: "#f1f5f9" }}>
                <tr>
                  <th align="left">Time</th>
                  <th align="left">Symbol</th>
                  <th align="left">Side</th>
                  <th align="right">Qty</th>
                  <th align="right">Price</th>
                  <th align="right">Fee</th>
                  <th align="right">PnL</th>
                </tr>
              </thead>
              <tbody>
                {trades.map((t) => (
                  <tr key={t.id} style={{ borderTop: "1px solid #e5e7eb" }}>
                    <td>{t.ts}</td>
                    <td>{t.symbol}</td>
                    <td>{t.side}</td>
                    <td align="right">{t.qty}</td>
                    <td align="right">{t.price}</td>
                    <td align="right">{t.fee}</td>
                    <td align="right">{t.pnl}</td>
                  </tr>
                ))}
                {trades.length === 0 && (
                  <tr>
                    <td colSpan="7" style={{ opacity: 0.7 }}>
                      (no trades yet)
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
