const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: "./src/main/typescript/index.tsx",

  output: {
    filename: "bundle.js",
    path: __dirname + "/target/classes/META-INF/resources"
  },

  devtool: "source-map",

  resolve: {
    extensions: [".ts", ".tsx", ".js", ".json"]
  },

  module: {
    rules: [
      { test: /\.tsx?$/, loader: "awesome-typescript-loader" },
      { enforce: "pre", test: /\.js$/, loader: "source-map-loader" }
    ],
  },

  plugins: [
    new HtmlWebpackPlugin({
      "template": "src/main/typescript/index.html"
    })
  ],

  devServer: {
    contentBase: "src/main/public/",
    proxy: {
      "/mav": {
        target: {
          host: "localhost",
          protocol: "http:",
          port: 8080
        }
      }
    }
  }
};
