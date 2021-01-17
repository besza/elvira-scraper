import * as React from "react"

import { ChartData, ChartPoint } from "chart.js"
import { Line } from "react-chartjs-2"

const colors = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99", "#b15928"]
  .map(s => s + "a0")

interface Train {
  origin: string,
  destination: string,
  plannedDeparture: string,
  delayMinutes: number
}

function fetchTrains(): Promise<ReadonlyArray<Train>> {
  return fetch("/mav").then(response => response.json())
}

function collectChartData(trains: ReadonlyArray<Train>): Record<string, ChartData> {
  let dataByTimeByLine: Record<string, Record<string, Array<ChartPoint>>> = {}
  trains.forEach(train => {
    const line = `${train.origin} - ${train.destination}`
    const dataByTime = dataByTimeByLine[line] || {}

    const [date, time] = train.plannedDeparture.split(" ")
    const isoDate = date.replaceAll(".", "-")
    const point = { t: isoDate, y: train.delayMinutes }
    dataByTimeByLine[line] = { ...dataByTime, [time]: [ ...(dataByTime[time] || []), point ] }
  })

  const datasets = Object.fromEntries(
    Object.entries(dataByTimeByLine).map(([line, dataByTime]) => {
      const entries = Object.entries(dataByTime)
      entries.sort(([a], [b]) => a < b ? -1 : 1)
      const datasets = entries.map(([time, data], index) =>
        ({
          borderColor: colors[index % 12],
          fill: false,
          label: time,
          data
        })
      )
      return [line, { datasets }]
    })
  )
  return datasets
}

export const App = () => {
  const [data, setData] = React.useState<Record<string, ChartData> | undefined>()
  const [line, setLine] = React.useState<string | undefined>()

  React.useEffect(() => {
    fetchTrains().then(ts => {
      const data = collectChartData(ts)
      setData(data)
      setLine(Object.keys(data)[0])
    })
  }, [])

  if (data && line) {
    const selectedData = { ...data[line] }
    return <div>
      <div>
        { Object.keys(data).map(key =>
          <div key={key}>
            <input type="radio" value={key} checked={line === key} onChange={() => setLine(key)} />
            <label>{key}</label>
          </div>
        )}
      </div>
      <Line data={selectedData} options={{scales: {xAxes: [{type: "time", time: {unit: "day"}}]}}} />
    </div>
  } else {
    return null
  }
}
