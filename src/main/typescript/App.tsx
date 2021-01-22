import * as React from "react"

import { ChartData, ChartPoint } from "chart.js"
import { Line } from "react-chartjs-2"

const colors = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#b15928"]
  .map(s => s + "a0")

interface Route {
  origin: string,
  destination: string
}

interface Train {
  plannedDeparture: string,
  delayMinutes: number
}

async function fetchRoutes(): Promise<ReadonlyArray<Route>> {
  const obj: Record<string, ReadonlyArray<string>> =
    await fetch("/mav/routes").then(response => response.json())
  const routes = Object.entries(obj).flatMap(([origin, destinations]) =>
    destinations.map(destination => ({ origin, destination }))
  )
  routes.sort((a, b) => a.origin + a.destination < b.origin + b.destination ? -1 : 1)
  return routes
}

function fetchTrains(route: Route): Promise<ReadonlyArray<Train>> {
  const uri = `/mav?from=${encodeURIComponent(route.origin)}&to=${encodeURIComponent(route.destination)}`
  return fetch(uri).then(response => response.json())
}

function collectChartData(trains: ReadonlyArray<Train>): ChartData {
  let dataByTime: Record<string, Array<ChartPoint>> = {}
  trains.forEach(train => {
    const [date, time] = train.plannedDeparture.split(" ")
    const isoDate = date.replaceAll(".", "-")
    const point = { t: isoDate, y: train.delayMinutes }
    dataByTime = { ...dataByTime, [time]: [ ...(dataByTime[time] || []), point ] }
  })

  const entries = Object.entries(dataByTime)
  entries.sort(([a], [b]) => a < b ? -1 : 1)
  const datasets = entries.map(([time, data], index) => {
    const hidden = time < "09:00" || time >= "17:00"
    return {
      borderColor: colors[index % colors.length],
      fill: false,
      label: time,
      hidden,
      data
    }
  })
  return { datasets }
}

interface State {
  routes: ReadonlyArray<Route>,
  selectedRoute?: Route,
  data: Map<Route, ChartData>
}

export const App = () => {
  const [state, setState] = React.useState<State>({ routes: [], data: new Map() })
  const { routes, selectedRoute, data } = state

  React.useEffect(() => {
    fetchRoutes().then(routes =>
      setState(state => ({ ...state, routes, selectedRoute: routes[0] }))
    )
  }, [])

  React.useEffect(() => {
    if (selectedRoute && !data.has(selectedRoute)) {
      fetchTrains(selectedRoute).then(ts => {
        const data = collectChartData(ts)
        setState(state => ({ ...state, data: new Map([...state.data, [selectedRoute, data]]) }) )
      })
    }
  }, [selectedRoute])

  const onRouteChange = (route: Route) =>
    setState(state => state.selectedRoute === route ? state : { ...state, selectedRoute: route })

  const selectedData = React.useMemo(() => {
    const chartData = selectedRoute && data.get(selectedRoute)
    return chartData && { ...chartData }
  }, [data, selectedRoute])

  return <div className="container">
    <div className="routes">
      { routes.map(route => {
          const key = `${route.origin} – ${route.destination}`
          return <div key={key}>
            <input type="radio" id={key} value={key} checked={route === selectedRoute} onChange={() => onRouteChange(route)} />
            <label htmlFor={key}>{key}</label>
          </div>
      })}
    </div>
    <div className="chart">
      { selectedData
          ? <Line data={selectedData} options={{maintainAspectRatio: false, scales: {xAxes: [{type: "time", time: {unit: "day"}}]}}} />
          : "Loading..."
      }
    </div>
  </div>
}
