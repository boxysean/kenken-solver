import React from 'react';
import './App.css';
import Board from './Board'

class App extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: new Set(),
      selecting: false,
    };
  }

  onMouseUp(event) {
    console.log("APP UP");
  }

  processRelease(cellIndex) {
    console.log("ehh?");
    this.setState({
      selected: new Set(),
      selecting: false,
    });
  }

  processHover(cellIndex) {
    // console.log("PROCESS HOVER", cellIndex);

    if (this.state.selecting) {
      this.setState({
        selected: this.state.selected.add(cellIndex)
      });
    }
  }

  processBegin(cellIndex) {
    console.log("PROCESS BEGIN", cellIndex);

    this.setState({
      selected: new Set([cellIndex]),
      selecting: true,
    })
  }

  render() {
    return (
      <div className="App" onMouseUp={(event) => this.onMouseUp(event)}>
        <header className="App-header">
          <Board
            size="4"
            processRelease={event => this.processRelease(event)}
            processHover={event => this.processHover(event)}
            processBegin={event => this.processBegin(event)}
            selectedCells={this.state.selected}
          ></Board>
        </header>
      </div>
    );
  }
}

export default App;
