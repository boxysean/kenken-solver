import React from 'react';
import { Spinner } from "react-loading-io";

import SolveLifecycle from "../SolveLifecycle";

import './Spinner.css';

class LoadingSpinner extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    var loadingDisplayStyle = {};

    if (!this.props.showSpinner) {
      loadingDisplayStyle['display'] = 'none';
    }

    return (
      <div className="LoadingSpinner" style={loadingDisplayStyle}>
        <Spinner color="#4CA7FD" size={100} />
      </div>
    );
  }
}

export default LoadingSpinner;
